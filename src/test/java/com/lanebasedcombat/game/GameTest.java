package com.lanebasedcombat.game;

import com.lanebasedcombat.model.Lane;
import com.lanebasedcombat.model.Player;
import com.lanebasedcombat.model.Wave;
import com.lanebasedcombat.model.enemy.Boss;
import com.lanebasedcombat.model.enemy.CommonEnemy;
import com.lanebasedcombat.model.enemy.Enemy;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Object getField(Game game, String fieldName) throws Exception {
        Field field = Game.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(game);
    }

    private void setField(Game game, String fieldName, Object value)
            throws Exception {
        Field field = Game.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(game, value);
    }

    private Object callMethod(Game game, String methodName, Class<?>[] types,
                              Object... arguments) throws Exception {
        Method method = Game.class.getDeclaredMethod(methodName, types);
        method.setAccessible(true);
        return method.invoke(game, arguments);
    }

    @Test
    void gameStartsWithThreeLives() throws Exception {
        Game game = new Game();
        Player player = (Player) getField(game, "player");

        assertEquals(3, player.getLives());
        assertTrue(player.isAlive());
    }

    @Test
    void gameStartsWithThreeEmptyLanes() throws Exception {
        Game game = new Game();

        @SuppressWarnings("unchecked")
        List<Lane> lanes = (List<Lane>) getField(game, "lanes");

        assertEquals(3, lanes.size());
        assertTrue(lanes.stream().allMatch(lane -> !lane.hasEnemies()));
    }

    @Test
    void gameStartsAtWaveOne() throws Exception {
        Game game = new Game();

        assertEquals(1, getField(game, "currentWaveNumber"));
        assertEquals(1, ((Wave) getField(game, "currentWave")).getNumber());
    }

    @Test
    void attackingLaneWithCommonEnemyDefeatsItAndAwardsPoints()
            throws Exception {
        Game game = new Game();
        Player player = (Player) getField(game, "player");

        @SuppressWarnings("unchecked")
        List<Lane> lanes = (List<Lane>) getField(game, "lanes");

        CommonEnemy enemy = new CommonEnemy();
        lanes.get(0).addEnemy(enemy);

        callMethod(game, "attackLane", new Class<?>[]{int.class}, 1);

        assertFalse(enemy.isAlive());
        assertEquals(100, player.getScore());
        assertTrue(lanes.get(0).getEnemies().isEmpty());
    }

    @Test
    void attackingEmptyLaneDoesNotAwardPoints() throws Exception {
        Game game = new Game();
        Player player = (Player) getField(game, "player");

        callMethod(game, "attackLane", new Class<?>[]{int.class}, 2);

        assertEquals(0, player.getScore());
    }

    @Test
    void attackingInvalidLaneDoesNotAwardPoints() throws Exception {
        Game game = new Game();
        Player player = (Player) getField(game, "player");

        callMethod(game, "attackLane", new Class<?>[]{int.class}, 4);

        assertEquals(0, player.getScore());
    }

    @Test
    void attackTargetsEnemyClosestToPlayer() throws Exception {
        Game game = new Game();

        @SuppressWarnings("unchecked")
        List<Lane> lanes = (List<Lane>) getField(game, "lanes");

        CommonEnemy fartherEnemy = new CommonEnemy();
        CommonEnemy closerEnemy = new CommonEnemy();

        fartherEnemy.move();
        closerEnemy.move();
        closerEnemy.move();

        lanes.get(0).addEnemy(fartherEnemy);
        lanes.get(0).addEnemy(closerEnemy);

        callMethod(game, "attackLane", new Class<?>[]{int.class}, 1);

        assertTrue(fartherEnemy.isAlive());
        assertFalse(closerEnemy.isAlive());
    }

    @Test
    void defeatedCommonEnemyIncrementsDefeatedAndResolvedCounts()
            throws Exception {
        Game game = new Game();

        callMethod(game, "attackLane", new Class<?>[]{int.class}, 1);

        @SuppressWarnings("unchecked")
        List<Lane> lanes = (List<Lane>) getField(game, "lanes");

        CommonEnemy enemy = new CommonEnemy();
        lanes.get(0).addEnemy(enemy);

        callMethod(game, "attackLane", new Class<?>[]{int.class}, 1);

        assertEquals(1, getField(game, "defeatedCommonEnemies"));
        assertEquals(1, getField(game, "resolvedCommonEnemies"));
    }

    @Test
    void bossDefeatAwardsFiveHundredPointsAndEndsBossBattle()
            throws Exception {
        Game game = new Game();
        Player player = (Player) getField(game, "player");

        @SuppressWarnings("unchecked")
        List<Lane> lanes = (List<Lane>) getField(game, "lanes");

        Boss boss = new Boss();
        lanes.get(1).addEnemy(boss);

        callMethod(game, "attackLane", new Class<?>[]{int.class}, 2);
        callMethod(game, "attackLane", new Class<?>[]{int.class}, 2);
        callMethod(game, "attackLane", new Class<?>[]{int.class}, 2);

        assertFalse(boss.isAlive());
        assertEquals(500, player.getScore());
        assertTrue((boolean) getField(game, "bossDefeated"));
    }

    @Test
    void movingEnemyToLastPositionDamagesPlayerAndResolvesEnemy()
            throws Exception {
        Game game = new Game();
        Player player = (Player) getField(game, "player");

        @SuppressWarnings("unchecked")
        List<Lane> lanes = (List<Lane>) getField(game, "lanes");

        CommonEnemy enemy = new CommonEnemy();

        for (int i = 0; i < 4; i++) {
            enemy.move();
        }

        lanes.get(0).addEnemy(enemy);

        callMethod(game, "moveEnemies", new Class<?>[]{});

        assertEquals(2, player.getLives());
        assertTrue(lanes.get(0).getEnemies().isEmpty());
        assertEquals(1, getField(game, "resolvedCommonEnemies"));
    }

    @Test
    void bossFormationContainsBossAndFourSupportEnemies()
            throws Exception {
        Game game = new Game();

        callMethod(game, "spawnBoss", new Class<?>[]{});

        @SuppressWarnings("unchecked")
        List<Lane> lanes = (List<Lane>) getField(game, "lanes");

        assertTrue((boolean) getField(game, "bossSpawned"));
        assertEquals(2, lanes.get(0).getEnemies().size());
        assertEquals(1, lanes.get(1).getEnemies().size());
        assertEquals(2, lanes.get(2).getEnemies().size());

        assertInstanceOf(Boss.class, lanes.get(1).getEnemies().get(0));

        assertEquals(0, lanes.get(0).getEnemies().get(0).getPosition());
        assertEquals(1, lanes.get(0).getEnemies().get(1).getPosition());
        assertEquals(0, lanes.get(2).getEnemies().get(0).getPosition());
        assertEquals(1, lanes.get(2).getEnemies().get(1).getPosition());
    }
}