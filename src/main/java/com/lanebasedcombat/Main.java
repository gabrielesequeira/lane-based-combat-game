package com.lanebasedcombat;

import com.lanebasedcombat.facade.GameFacade;

public class Main {

    public static void main(String[] args) {
        GameFacade facade = new GameFacade();
        facade.startGame();
    }
}
