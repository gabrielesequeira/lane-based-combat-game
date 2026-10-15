
package com.lanebasedcombat.model;

import com.lanebasedcombat.model.enemy.Enemy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lane {

    private static final int TOTAL_POSITIONS = 6;
    private static final int PLAYER_ROW = 6;

    private final int number;
    private final List<Enemy> enemies;

    public Lane(int number) {
        if (number < 1 || number > 3) {
            throw new IllegalArgumentException(
                    "A pista deve ser um numero entre 1 e 3."
            );
        }

        this.number = number;
        this.enemies = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }

    public List<Enemy> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }

    public void addEnemy(Enemy enemy) {
        if (enemy != null) {
            enemies.add(enemy);
        }
    }

    public boolean hasEnemies() {
        return !enemies.isEmpty();
    }

    public void removeDefeatedEnemies() {
        enemies.removeIf(enemy -> !enemy.isAlive());
    }

    public List<Enemy> moveEnemies() {
        List<Enemy> reachedPlayer = new ArrayList<>();

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }

            enemy.move();

            if (enemy.getPosition() >= PLAYER_ROW - 1) {
                reachedPlayer.add(enemy);
            }
        }

        enemies.removeAll(reachedPlayer);

        return reachedPlayer;
    }

    public int getTotalPositions() {
        return TOTAL_POSITIONS;
    }
}
