
package com.lanebasedcombat.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lanebasedcombat.factory.BossFactory;
import com.lanebasedcombat.factory.CommonEnemyFactory;
import com.lanebasedcombat.factory.EnemyFactory;
import com.lanebasedcombat.model.Lane;
import com.lanebasedcombat.model.Player;
import com.lanebasedcombat.model.Wave;
import com.lanebasedcombat.model.enemy.Enemy;
import com.lanebasedcombat.strategy.AttackStrategy;
import com.lanebasedcombat.strategy.BasicAttack;
import com.lanebasedcombat.ui.GameConsole;

public class Game {

    private static final int TOTAL_WAVES = 3;
    private static final int ENEMIES_PER_WAVE = 3;
    private static final int TOTAL_COMMON_ENEMIES =
            TOTAL_WAVES * ENEMIES_PER_WAVE;

    private static final int ROWS = 6;
    private static final int COLUMNS = 3;

    // Probabilidade de surgir um inimigo em cada turno.
    private static final double SPAWN_CHANCE = 0.60;

    private final Player player;
    private final List<Lane> lanes;
    private final GameConsole console;
    private final AttackStrategy attackStrategy;
    private final EnemyFactory commonEnemyFactory;
    private final EnemyFactory bossFactory;
    private final Random random;

    private Wave currentWave;
    private int currentWaveNumber;
    private int spawnedCommonEnemies;
    private int defeatedCommonEnemies;
    private int resolvedCommonEnemies;

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
        attackStrategy = new BasicAttack();
        commonEnemyFactory = new CommonEnemyFactory();
        bossFactory = new BossFactory();
        random = new Random();

        currentWaveNumber = 1;
        currentWave = new Wave(currentWaveNumber, ENEMIES_PER_WAVE);

        spawnedCommonEnemies = 0;
        defeatedCommonEnemies = 0;
        resolvedCommonEnemies = 0;

        bossSpawned = false;
        bossDefeated = false;
        bossReachedPlayer = false;
    }

    public void start() {
        console.showWelcome();

        while (player.isAlive()
                && !bossDefeated
                && !bossReachedPlayer) {

            advanceWaveIfNecessary();
            spawnEnemy();
            showGameState();

            int selectedLane = console.askLane();

            attackLane(selectedLane);

            if (bossDefeated) {
                break;
            }

            moveEnemies();

            if (!player.isAlive() || bossReachedPlayer) {
                break;
            }

            advanceWaveIfNecessary();
        }

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

        int enemiesResolvedInCurrentWaves =
                currentWaveNumber * ENEMIES_PER_WAVE;

        if (currentWave.isFinishedSpawning()
                && resolvedCommonEnemies >= enemiesResolvedInCurrentWaves) {

            currentWaveNumber++;

            currentWave = new Wave(
                    currentWaveNumber,
                    ENEMIES_PER_WAVE
            );

            console.showMessage(
                    "Onda " + (currentWaveNumber - 1)
                    + " concluida! Iniciando a onda "
                    + currentWaveNumber + "."
            );
        }
    }

    private void spawnEnemy() {
        if (spawnedCommonEnemies < TOTAL_COMMON_ENEMIES) {

            if (!currentWave.hasEnemiesToSpawn()) {
                return;
            }

            // O inimigo só aparece se o sorteio permitir.
            if (random.nextDouble() >= SPAWN_CHANCE) {
                console.showMessage(
                        "Nenhum inimigo novo apareceu neste turno."
                );
                return;
            }

            Enemy enemy = commonEnemyFactory.createEnemy();

            Lane lane = lanes.get(random.nextInt(COLUMNS));
            lane.addEnemy(enemy);

            currentWave.registerSpawn();
            spawnedCommonEnemies++;

            console.showMessage(
                    "Onda " + currentWaveNumber
                    + ": inimigo comum apareceu na pista "
                    + lane.getNumber() + "!"
            );

        } else if (!bossSpawned
                && defeatedCommonEnemies == TOTAL_COMMON_ENEMIES
                && allLanesAreEmpty()) {

            spawnBoss();
        }
    }

    private void spawnBoss() {
        Enemy boss = bossFactory.createEnemy();

        Lane lane = lanes.get(random.nextInt(COLUMNS));
        lane.addEnemy(boss);

        bossSpawned = true;

        console.showMessage(
                "O CHEFE FINAL apareceu na pista "
                + lane.getNumber() + "!"
        );
    }

    private void showGameState() {
        console.showMessage("");
        console.showMessage("========== TABULEIRO ==========");
        console.showMessage(
                "Onda: " + currentWaveNumber
                + " | Vidas: " + player.getLives()
                + " | Pontuacao: " + player.getScore()
        );

        console.showMessage("      PISTA 1    PISTA 2    PISTA 3");
        console.showMessage("    +----------+----------+----------+");

        for (int row = 0; row < ROWS; row++) {
            StringBuilder line = new StringBuilder();

            line.append(String.format("%2d  |", row + 1));

            for (int column = 0; column < COLUMNS; column++) {
                String cell = getCellContent(row, column);

                line.append(String.format(" %-8s |", cell));
            }

            console.showMessage(line.toString());
            console.showMessage("    +----------+----------+----------+");
        }

        console.showMessage("      (7,1)      (7,2)      (7,3)");
        console.showMessage("       [1]        [2]        [3]");
        console.showMessage("          POSICOES DE ATAQUE");
        console.showMessage("================================");
        console.showMessage("");
    }

    private String getCellContent(int row, int column) {
        Lane lane = lanes.get(column);
        List<Enemy> enemies = lane.getEnemies();

        int count = 0;
        boolean hasBoss = false;

        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && enemy.getPosition() == row) {
                count++;

                if (enemy.getName().equals("Chefe final")) {
                    hasBoss = true;
                }
            }
        }

        if (count == 0) {
            return ".";
        }

        if (hasBoss) {
            return count > 1 ? "B+" + (count - 1) : "CHEFE";
        }

        return count > 1 ? "I x" + count : "INIMIGO";
    }

    private void attackLane(int laneNumber) {
        Lane lane = lanes.get(laneNumber - 1);
        Enemy target = findClosestEnemy(lane);

        if (target == null) {
            console.showMessage(
                    "A pista " + laneNumber
                    + " esta vazia. O ataque nao causou dano."
            );
            return;
        }

        int previousHealth = target.getHealth();

        attackStrategy.attack(target);

        console.showMessage(
                "Voce atacou " + target.getName()
                + " na pista " + laneNumber + "."
        );

        if (!target.isAlive()) {
            player.addScore(target.getPoints());

            if (target.getName().equals("Chefe final")) {
                bossDefeated = true;
                console.showMessage("Chefe derrotado!");
            } else {
                defeatedCommonEnemies++;
                resolvedCommonEnemies++;
                console.showMessage("Inimigo derrotado!");
            }

            console.showMessage(
                    "Voce ganhou " + target.getPoints() + " pontos."
            );

            lane.removeDefeatedEnemies();

        } else {
            console.showMessage(
                    "Vida do inimigo: "
                    + previousHealth + " -> "
                    + target.getHealth()
            );
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

                console.showMessage(
                        enemy.getName()
                        + " alcancou o jogador! Perdeu "
                        + enemy.getDamage() + " vida(s)."
                );

                if (enemy.getName().equals("Chefe final")) {
                    bossReachedPlayer = true;

                    console.showMessage(
                            "O chefe alcancou o jogador! Fim de jogo."
                    );
                } else {
                    resolvedCommonEnemies++;
                }
            }

            if (bossReachedPlayer) {
                return;
            }

            if (!player.isAlive()) {
                return;
            }
        }
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
