package com.lanebasedcombat.model;

public class Wave {
    private final int number;
    private final int totalEnemies;
    private int spawnedEnemies;

    public Wave(int number, int totalEnemies) {
        this.number = number;
        this.totalEnemies = totalEnemies;
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

    public void registerSpawn() {
        if (hasEnemiesToSpawn()) {
            spawnedEnemies++;
        }
    }

    public boolean isFinishedSpawning() {
        return spawnedEnemies >= totalEnemies;
    }
}