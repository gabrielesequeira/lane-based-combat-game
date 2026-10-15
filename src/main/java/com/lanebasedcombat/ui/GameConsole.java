
package com.lanebasedcombat.ui;

import java.util.Scanner;

public class GameConsole {

    private final Scanner scanner;

    public GameConsole() {
        this.scanner = new Scanner(System.in);
    }

    public void showWelcome() {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("          LANE-BASED COMBAT GAME");
        System.out.println("==========================================");
        System.out.println("Defenda-se dos inimigos nas tres pistas!");
        System.out.println("Elimine tres ondas e derrote o chefe final.");
        System.out.println();
        System.out.println("COMO JOGAR:");
        System.out.println("- Escolha a pista 1, 2 ou 3 para atacar.");
        System.out.println("- Os inimigos avancam a cada turno.");
        System.out.println("- Cada inimigo comum derrotado vale 100 pontos.");
        System.out.println("- O chefe final vale 500 pontos.");
        System.out.println("==========================================");
    }

    public void showStatus(int lives, int score) {
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println("VIDAS: " + lives + " | PONTUACAO: " + score);
        System.out.println("------------------------------------------");
    }

    public int askLane() {
        while (true) {
            System.out.print(
                    "Escolha a posicao de ataque "
                    + "(7,1 = 1 | 7,2 = 2 | 7,3 = 3): "
            );

            if (scanner.hasNextInt()) {
                int lane = scanner.nextInt();

                if (lane >= 1 && lane <= 3) {
                    return lane;
                }
            } else {
                scanner.next();
            }

            System.out.println(
                    "Opcao invalida. Digite 1, 2 ou 3."
            );
        }
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showGameOver(int score) {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("                FIM DE JOGO");
        System.out.println("Voce nao conseguiu defender sua posicao.");
        System.out.println("Pontuacao final: " + score);
        System.out.println("==========================================");
    }

    public void showVictory(int score) {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("           PARABENS! VOCE VENCEU!");
        System.out.println("Voce derrotou todas as ondas e o chefe!");
        System.out.println("Pontuacao final: " + score);
        System.out.println("==========================================");
    }
}
