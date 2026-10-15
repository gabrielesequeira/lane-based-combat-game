package com.lanebasedcombat.model.enemy;

public abstract class Enemy {
    private final String name;
    private int health;
    private final int damage;
    private int position;

    protected Enemy(String name, int health, int damage) {
        this.name = name;
        this.health = health;
        this.damage = damage;
        this.position = 0;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public int getPosition() {
        return position;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage(int damage) {
        if (damage > 0 && isAlive()) {
            health = Math.max(0, health - damage);
        }
    }

    public void move() {
        position++;
    }

    public abstract int getPoints();
}
