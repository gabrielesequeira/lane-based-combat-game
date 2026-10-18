package com.lanebasedcombat.model;

import java.util.Objects;

import com.lanebasedcombat.model.enemy.Enemy;
import com.lanebasedcombat.strategy.AttackStrategy;
import com.lanebasedcombat.strategy.BasicAttack;

public class Player {
    private int lives;
    private int score;
    private final AttackStrategy attackStrategy;

    public Player() {
        this(new BasicAttack());
    }

    public Player(AttackStrategy attackStrategy) {
        this.lives = 3;
        this.score = 0;
        this.attackStrategy = Objects.requireNonNull(
                attackStrategy,
                "A estrategia de ataque nao pode ser nula."
        );
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public void takeDamage(int damage) {
        if (damage > 0) {
            lives = Math.max(0, lives - damage);
        }
    }

    public void addScore(int points) {
        if (points > 0) {
            score += points;
        }
    }

    public void attack(Enemy enemy) {
        if (isAlive() && enemy != null && enemy.isAlive()) {
            attackStrategy.attack(enemy);
        }
    }
}
