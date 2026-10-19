package com.lanebasedcombat.facade;

import com.lanebasedcombat.game.Game;

/**
 * Facade that provides a simple entry point to the game.
 * Hides the creation and startup details of the game subsystem.
 */
public class GameFacade {

    private final Game game;

    public GameFacade() {
        this.game = new Game();
    }

    public void startGame() {
        game.start();
    }
}
