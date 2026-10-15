package com.lanebasedcombat.strategy;

import com.lanebasedcombat.model.enemy.Enemy;

public class BasicAttack implements AttackStrategy {
    private static final int DAMAGE = 1;

    @Override
    public void attack(Enemy enemy) {
        if (enemy != null && enemy.isAlive()) {
            enemy.takeDamage(DAMAGE);
        }
    }
}
