package com.lanebasedcombat.factory;

import com.lanebasedcombat.model.enemy.Boss;
import com.lanebasedcombat.model.enemy.Enemy;

public class BossFactory extends EnemyFactory {
    @Override
    public Enemy createEnemy() {
        return new Boss();
    }
}
