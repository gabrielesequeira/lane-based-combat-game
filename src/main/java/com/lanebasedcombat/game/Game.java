package com.lanebasedcombat.game;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.lanebasedcombat.factory.BossFactory;
import com.lanebasedcombat.factory.CommonEnemyFactory;
import com.lanebasedcombat.factory.EnemyFactory;
import com.lanebasedcombat.model.Lane;
import com.lanebasedcombat.model.Player;
import com.lanebasedcombat.model.Wave;
import com.lanebasedcombat.model.enemy.Boss;
import com.lanebasedcombat.model.enemy.Enemy;
import com.lanebasedcombat.ui.GameConsole;

public class Game {
    private static final int TOTAL_WAVES = 3;
    private static final int ENEMIES_PER_WAVE = 10;
    private static final int TOTAL_COMMON_ENEMIES =
            TOTAL_WAVES * ENEMIES_PER_WAVE;
    private static final int ROWS = 6;
    private static final int COLUMNS = 3;
    private static final int MAX_LIVES = 3;
    private static final int HISTORY_LIMIT = 6;

    private final Player player;
    private final List<Lane> lanes;
    private final GameConsole console;
    private final EnemyFactory commonEnemyFactory;
    private final EnemyFactory bossFactory;
    private final Map<Enemy, Integer> enemyIds;
    private final List<String> history;

    private Wave currentWave;
    private int currentWaveNumber;
    private int spawnedCommonEnemies;
    private int defeatedCommonEnemies;
    private int resolvedCommonEnemies;
    private int nextEnemyId;
    private int turnNumber;

    private boolean bossSpawned;
    private boolean bossDefeated;
    private boolean bossReachedPlayer;

    public Game() {
        player = new Player();
        lanes = new ArrayList<>();

        for (int i = 1; i <= COLUMNS; i++) {
            lanes.add(new Lane(i));
        }

        console = new GameConsole();
        commonEnemyFactory = new CommonEnemyFactory();
        bossFactory = new BossFactory();
        enemyIds = new IdentityHashMap<>();
        history = new ArrayList<>();

        currentWaveNumber = 1;
        currentWave = new Wave(
                currentWaveNumber, ENEMIES_PER_WAVE, commonEnemyFactory);

        spawnedCommonEnemies = 0;
        defeatedCommonEnemies = 0;
        resolvedCommonEnemies = 0;
        nextEnemyId = 1;
        turnNumber = 0;

        bossSpawned = false;
        bossDefeated = false;
        bossReachedPlayer = false;
    }

    public void start() {
        console.showWelcome();

        while (player.isAlive() && !bossDefeated && !bossReachedPlayer) {
            advanceWaveIfNecessary();
            turnNumber++;

            spawnEnemies();
            showGameState();

            int selectedLane = console.askLane();
            addHistory("Turno " + turnNumber
                    + ": ataque escolhido na pista " + selectedLane + ".");

            attackLane(selectedLane);

            if (bossDefeated || !player.isAlive()) {
                break;
            }

            moveEnemies();

            if (!player.isAlive() || bossReachedPlayer) {
                break;
            }

            advanceWaveIfNecessary();
        }

        console.showHistory(getRecentHistory());

        if (player.isAlive() && bossDefeated) {
            console.showVictory(player.getScore());
        } else {
            console.showGameOver(player.getScore());
        }
    }

    private void advanceWaveIfNecessary() {
        if (currentWaveNumber >= TOTAL_WAVES) {
            return;
        }

        int requiredResolved = currentWaveNumber * ENEMIES_PER_WAVE;

        if (currentWave.isFinishedSpawning()
                && resolvedCommonEnemies >= requiredResolved) {
            int completedWave = currentWaveNumber;
            currentWaveNumber++;

            currentWave = new Wave(
                    currentWaveNumber, ENEMIES_PER_WAVE, commonEnemyFactory);

            String message = "Onda " + completedWave
                    + " concluida! Iniciando a onda "
                    + currentWaveNumber + ".";
            console.showMessage(message);
            addHistory(message);
        }
    }

    private void spawnEnemies() {
        if (bossSpawned) {
            return;
        }

        if (currentWaveNumber == TOTAL_WAVES
                && currentWave.isFinishedSpawning()
                && resolvedCommonEnemies >= TOTAL_COMMON_ENEMIES
                && allLanesAreEmpty()) {
            spawnBoss();
            return;
        }

        if (!currentWave.hasEnemiesToSpawn()) {
            return;
        }

        int amount = ThreadLocalRandom.current().nextInt(1, 3);

        for (int i = 0; i < amount; i++) {
            if (!currentWave.hasEnemiesToSpawn()) {
                break;
            }

            List<Lane> availableLanes = new ArrayList<>();

            for (Lane lane : lanes) {
                boolean spawnPositionOccupied = false;

                for (Enemy enemy : lane.getEnemies()) {
                    if (enemy.isAlive() && enemy.getPosition() == 0) {
                        spawnPositionOccupied = true;
                        break;
                    }
                }

                if (!spawnPositionOccupied) {
                    availableLanes.add(lane);
                }
            }

            if (availableLanes.isEmpty()) {
                break;
            }

            Lane lane = availableLanes.get(
                    ThreadLocalRandom.current().nextInt(
                            availableLanes.size()));

            Enemy enemy = currentWave.createNextEnemy();
            lane.addEnemy(enemy);
            enemyIds.put(enemy, nextEnemyId++);
            spawnedCommonEnemies++;

            String message = describeEnemy(enemy)
                    + " apareceu na pista " + lane.getNumber() + ".";
            console.showMessage(message);
            addHistory(message);
        }
    }

    private void spawnBoss() {
        if (bossSpawned) {
            return;
        }

        // A formaÃ§Ã£o final sempre ocupa as trÃªs pistas.
        Lane leftLane = lanes.get(0);
        Lane centerLane = lanes.get(1);
        Lane rightLane = lanes.get(2);

        // Primeira linha: INIMIGO - CHEFE - INIMIGO
        Enemy leftFront = commonEnemyFactory.createEnemy();
        leftLane.addEnemy(leftFront);
        enemyIds.put(leftFront, nextEnemyId++);

        Enemy boss = bossFactory.createEnemy();
        centerLane.addEnemy(boss);
        enemyIds.put(boss, nextEnemyId++);

        Enemy rightFront = commonEnemyFactory.createEnemy();
        rightLane.addEnemy(rightFront);
        enemyIds.put(rightFront, nextEnemyId++);

        // Segunda linha: INIMIGO - --- - INIMIGO
        // ComeÃ§am na posiÃ§Ã£o 1 para aparecerem na linha seguinte.
        spawnBossSupport(leftLane);
        spawnBossSupport(rightLane);

        bossSpawned = true;

        String message = "CHEFE FINAL apareceu na pista central!";
        console.showMessage(message);
        addHistory(message);

        String formationMessage =
                "O chefe trouxe quatro inimigos de apoio. Prepare-se!";
        console.showMessage(formationMessage);
        addHistory(formationMessage);
    }

    private void spawnBossSupport(Lane lane) {
        Enemy support = commonEnemyFactory.createEnemy();
        support.move();
        lane.addEnemy(support);
        enemyIds.put(support, nextEnemyId++);
    }
    private void showGameState() {
        String stage = bossSpawned
                ? "CHEFE FINAL"
                : "ONDA " + currentWaveNumber + "/" + TOTAL_WAVES;

        console.showTurnHeader(
                stage,
                turnNumber,
                player.getLives(),
                MAX_LIVES,
                player.getScore(),
                countRemainingEnemies(),
                getRecentHistory());

        String[][] board = new String[ROWS][COLUMNS];

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                board[row][column] = getCellContent(row, column);
            }
        }

        console.showBoard(board, ROWS, COLUMNS);
    }

    private String getCellContent(int row, int column) {
        StringBuilder content = new StringBuilder();

        Lane lane = lanes.get(column);

        for (Enemy enemy : lane.getEnemies()) {
            if (!enemy.isAlive() || enemy.getPosition() != row) {
                continue;
            }

            if (content.length() > 0) {
                content.append(" ");
            }

            if (enemy instanceof Boss) {
                content.append("[CHEFE]");
            } else {
                content.append(String.format(
                        "[E%02d]", getEnemyId(enemy)));
            }

            content.append("(")
                    .append(enemy.getHealth())
                    .append("HP)");
        }

        return content.length() == 0 ? "." : content.toString();
    }

    private void attackLane(int laneNumber) {
        if (laneNumber < 1 || laneNumber > COLUMNS) {
            console.showMessage("Pista invalida.");
            return;
        }

        Lane lane = lanes.get(laneNumber - 1);
        Enemy target = findClosestEnemy(lane);

        if (target == null) {
            String message = "Nenhum inimigo vivo na pista "
                    + laneNumber + ".";
            console.showMessage(message);
            addHistory(message);
            return;
        }

        player.attack(target);

        if (!target.isAlive()) {
            player.addScore(target.getPoints());

            String message = describeEnemy(target)
                    + " derrotado! +" + target.getPoints() + " pontos.";
            console.showMessage(message);
            addHistory(message);

            if (target instanceof Boss) {
                bossDefeated = true;
                String victory = "O chefe final foi derrotado!";
                console.showMessage(victory);
                addHistory(victory);
            } else {
                defeatedCommonEnemies++;
                resolvedCommonEnemies++;
            }

            lane.removeDefeatedEnemies();
        } else {
            String message = describeEnemy(target)
                    + " recebeu um ataque. Vida restante: "
                    + target.getHealth() + ".";
            console.showMessage(message);
            addHistory(message);
        }
    }

    private Enemy findClosestEnemy(Lane lane) {
        Enemy closest = null;

        for (Enemy enemy : lane.getEnemies()) {
            if (enemy.isAlive()
                    && (closest == null
                    || enemy.getPosition() > closest.getPosition())) {
                closest = enemy;
            }
        }

        return closest;
    }

    private void moveEnemies() {
        for (Lane lane : lanes) {
            List<Enemy> reachedPlayer = lane.moveEnemies();

            for (Enemy enemy : reachedPlayer) {
                player.takeDamage(enemy.getDamage());

                String message = describeEnemy(enemy)
                        + " alcancou o jogador. Dano recebido: "
                        + enemy.getDamage() + ".";
                console.showMessage(message);
                addHistory(message);

                if (enemy instanceof Boss) {
                    bossReachedPlayer = true;
                    String bossMessage =
                            "O chefe alcancou o jogador. Fim de jogo.";
                    console.showMessage(bossMessage);
                    addHistory(bossMessage);
                } else {
                    resolvedCommonEnemies++;
                }
            }

            if (bossReachedPlayer || !player.isAlive()) {
                return;
            }
        }
    }

    private int countRemainingEnemies() {
        if (bossSpawned) {
            int remaining = 0;

            for (Lane lane : lanes) {
                for (Enemy enemy : lane.getEnemies()) {
                    if (enemy.isAlive()) {
                        remaining++;
                    }
                }
            }

            return remaining;
        }

        return Math.max(
                0, TOTAL_COMMON_ENEMIES - resolvedCommonEnemies);
    }

    private String describeEnemy(Enemy enemy) {
        String type = enemy instanceof Boss ? "Chefe" : "Inimigo";
        return type + " " + getEnemyId(enemy);
    }

    private int getEnemyId(Enemy enemy) {
        Integer id = enemyIds.get(enemy);

        if (id == null) {
            id = nextEnemyId++;
            enemyIds.put(enemy, id);
        }

        return id;
    }

    private void addHistory(String message) {
        history.add(message);
    }

    private List<String> getRecentHistory() {
        int start = Math.max(0, history.size() - HISTORY_LIMIT);
        return new ArrayList<>(history.subList(start, history.size()));
    }

    private boolean allLanesAreEmpty() {
        for (Lane lane : lanes) {
            if (lane.hasEnemies()) {
                return false;
            }
        }

        return true;
    }
}
