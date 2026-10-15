package com.lanebasedcombat.model.enemy;

public class CommonEnemy extends Enemy {

    public CommonEnemy() {
        super("Inimigo comum", 1, 1);
    }

    @Override
    public int getPoints() {
        return 100;
    }
}
