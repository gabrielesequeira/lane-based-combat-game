package com.lanebasedcombat.model.enemy;

public class Boss extends Enemy {

    public Boss() {
        super("Chefe final", 3, 1);
    }

    @Override
    public int getPoints() {
        return 500;
    }
}
