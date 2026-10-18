package com.lanebasedcombat.progression;

import com.lanebasedcombat.factory.CommonEnemyFactory;
import com.lanebasedcombat.model.Wave;
import com.lanebasedcombat.model.enemy.CommonEnemy;
import com.lanebasedcombat.model.enemy.Enemy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProgressionTest {

    @Test
    void firstWaveStartsWithNumberOne() {
        Wave wave = new Wave(1, 10, new CommonEnemyFactory());

        assertEquals(1, wave.getNumber());
    }

    @Test
    void waveStartsWithNoEnemiesSpawned() {
        Wave wave = new Wave(1, 10, new CommonEnemyFactory());

        assertEquals(0, wave.getSpawnedEnemies());
    }

    @Test
    void waveAllowsSpawningTenEnemies() {
        Wave wave = new Wave(1, 10, new CommonEnemyFactory());

        for (int i = 0; i < 10; i++) {
            assertNotNull(wave.createNextEnemy());
        }

        assertEquals(10, wave.getSpawnedEnemies());
        assertTrue(wave.isFinishedSpawning());
    }

    @Test
    void waveDoesNotSpawnMoreThanItsLimit() {
        Wave wave = new Wave(2, 10, new CommonEnemyFactory());

        for (int i = 0; i < 10; i++) {
            wave.createNextEnemy();
        }

        assertNull(wave.createNextEnemy());
        assertEquals(10, wave.getSpawnedEnemies());
    }

    @Test
    void createdEnemiesAreCommonEnemies() {
        Wave wave = new Wave(1, 10, new CommonEnemyFactory());

        Enemy enemy = wave.createNextEnemy();

        assertInstanceOf(CommonEnemy.class, enemy);
    }

    @Test
    void registeringSpawnsFinishesWaveAtItsLimit() {
        Wave wave = new Wave(3, 10, new CommonEnemyFactory());

        for (int i = 0; i < 10; i++) {
            wave.registerSpawn();
        }

        assertEquals(10, wave.getSpawnedEnemies());
        assertFalse(wave.hasEnemiesToSpawn());
        assertTrue(wave.isFinishedSpawning());
    }
}