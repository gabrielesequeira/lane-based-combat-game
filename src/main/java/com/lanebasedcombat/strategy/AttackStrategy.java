package com.lanebasedcombat.strategy;

import com.lanebasedcombat.model.enemy.Enemy;

public interface AttackStrategy {
    void attack(Enemy enemy);
}
