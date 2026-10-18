package com.lanebasedcombat.model;

import java.util.Objects;

import com.lanebasedcombat.factory.EnemyFactory;
import com.lanebasedcombat.model.enemy.Enemy;

public class Wave {
    private final int number;
    private final int totalEnemies;
    private final EnemyFactory enemyFactory;
    private int spawnedEnemies;

    public Wave(int number, int totalEnemies, EnemyFactory enemyFactory) {
        if (number < 1) {
            throw new IllegalArgumentException(
                    "O numero da onda deve ser positivo."
            );
        }

        if (totalEnemies < 0) {
            throw new IllegalArgumentException(
                    "A quantidade de inimigos nao pode ser negativa."
            );
        }

        this.number = number;
        this.totalEnemies = totalEnemies;
        this.enemyFactory = Objects.requireNonNull(
                enemyFactory,
                "A fabrica de inimigos nao pode ser nula."
        );
        this.spawnedEnemies = 0;
    }

    public int getNumber() {
        return number;
    }

    public int getSpawnedEnemies() {
        return spawnedEnemies;
    }

    public boolean hasEnemiesToSpawn() {
        return spawnedEnemies < totalEnemies;
    }

    public Enemy createNextEnemy() {
        if (!hasEnemiesToSpawn()) {
            return null;
        }

        Enemy enemy = Objects.requireNonNull(
                enemyFactory.createEnemy(),
                "A fabrica nao pode retornar um inimigo nulo."
        );

        spawnedEnemies++;
        return enemy;
    }

    public void registerSpawn() {
        if (hasEnemiesToSpawn()) {
            spawnedEnemies++;
        }
    }

    public boolean isFinishedSpawning() {
        return !hasEnemiesToSpawn();
    }
}