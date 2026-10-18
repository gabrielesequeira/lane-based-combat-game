package com.lanebasedcombat.model;

import com.lanebasedcombat.model.enemy.CommonEnemy;
import com.lanebasedcombat.model.enemy.Enemy;
import com.lanebasedcombat.strategy.BasicAttack;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LaneTest {

    @Test
    void laneStartsEmpty() {
        Lane lane = new Lane(1);

        assertFalse(lane.hasEnemies());
        assertTrue(lane.getEnemies().isEmpty());
    }

    @Test
    void addingEnemyStoresItInLane() {
        Lane lane = new Lane(1);
        CommonEnemy enemy = new CommonEnemy();

        lane.addEnemy(enemy);

        assertTrue(lane.hasEnemies());
        assertEquals(1, lane.getEnemies().size());
        assertSame(enemy, lane.getEnemies().get(0));
    }

    @Test
    void movingEnemiesAdvancesPositionByOne() {
        Lane lane = new Lane(1);
        CommonEnemy enemy = new CommonEnemy();
        lane.addEnemy(enemy);

        lane.moveEnemies();

        assertEquals(1, enemy.getPosition());
    }

    @Test
    void defeatedEnemiesCanBeRemovedFromLane() {
        Lane lane = new Lane(1);
        CommonEnemy enemy = new CommonEnemy();
        lane.addEnemy(enemy);
        new BasicAttack().attack(enemy);

        lane.removeDefeatedEnemies();

        assertTrue(lane.getEnemies().isEmpty());
        assertFalse(lane.hasEnemies());
    }

    @Test
    void enemyReachingPlayerIsReturnedAndRemoved() {
        Lane lane = new Lane(1);
        CommonEnemy enemy = new CommonEnemy();
        lane.addEnemy(enemy);

        for (int i = 0; i < 4; i++) {
            lane.moveEnemies();
        }

        assertEquals(4, enemy.getPosition());
        assertTrue(lane.getEnemies().contains(enemy));

        List<Enemy> reached = lane.moveEnemies();

        assertEquals(1, reached.size());
        assertSame(enemy, reached.get(0));
        assertTrue(lane.getEnemies().isEmpty());
        assertEquals(5, enemy.getPosition());
    }

    @Test
    void laneRejectsNumbersOutsideOneToThree() {
        assertThrows(IllegalArgumentException.class, () -> new Lane(0));
        assertThrows(IllegalArgumentException.class, () -> new Lane(4));
    }
}