package com.lanebasedcombat.factory;

import com.lanebasedcombat.model.enemy.CommonEnemy;
import com.lanebasedcombat.model.enemy.Enemy;

public class CommonEnemyFactory extends EnemyFactory {
    @Override
    public Enemy createEnemy() {
        return new CommonEnemy();
    }
}
