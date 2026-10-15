package com.lanebasedcombat.model;

public class Player {
    private int lives;
    private int score;

    public Player() {
        this.lives = 3;
        this.score = 0;
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
}