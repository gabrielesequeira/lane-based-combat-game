package com.lanebasedcombat.combat;

import com.lanebasedcombat.model.Lane;
import com.lanebasedcombat.model.Player;
import com.lanebasedcombat.model.enemy.Boss;
import com.lanebasedcombat.model.enemy.CommonEnemy;
import com.lanebasedcombat.strategy.BasicAttack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CombatTest {

    @Test
    void commonEnemyIsDefeatedByOneBasicAttack() {
        CommonEnemy enemy = new CommonEnemy();

        new BasicAttack().attack(enemy);

        assertFalse(enemy.isAlive());
        assertEquals(0, enemy.getHealth());
    }

    @Test
    void bossStartsWithThreeHealthPoints() {
        Boss boss = new Boss();

        assertEquals(3, boss.getHealth());
        assertTrue(boss.isAlive());
    }

    @Test
    void bossLosesOneHealthPointPerBasicAttack() {
        Boss boss = new Boss();
        BasicAttack attack = new BasicAttack();

        attack.attack(boss);

        assertEquals(2, boss.getHealth());
        assertTrue(boss.isAlive());
    }

    @Test
    void bossIsDefeatedAfterThreeBasicAttacks() {
        Boss boss = new Boss();
        BasicAttack attack = new BasicAttack();

        attack.attack(boss);
        attack.attack(boss);
        attack.attack(boss);

        assertEquals(0, boss.getHealth());
        assertFalse(boss.isAlive());
    }

    @Test
    void commonEnemyAwardsOneHundredPoints() {
        CommonEnemy enemy = new CommonEnemy();

        assertEquals(100, enemy.getPoints());
    }

    @Test
    void bossAwardsFiveHundredPoints() {
        Boss boss = new Boss();

        assertEquals(500, boss.getPoints());
    }

    @Test
    void attackingAnEmptyLaneDoesNotCreateEnemies() {
        Lane lane = new Lane(1);

        assertFalse(lane.hasEnemies());
        assertTrue(lane.getEnemies().isEmpty());
    }
}