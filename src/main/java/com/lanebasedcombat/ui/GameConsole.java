package com.lanebasedcombat.ui;

import java.util.Scanner;

public class GameConsole {
    private final Scanner scanner;

    public GameConsole() {
        scanner = new Scanner(System.in);
    }

    public void showWelcome() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("             LANE-BASED COMBAT GAME");
        System.out.println("==================================================");
        System.out.println();
        System.out.println("REGRAS DO JOGO");
        System.out.println("- Defenda-se dos inimigos em três pistas.");
        System.out.println("- Sobreviva a três ondas com dez inimigos cada.");
        System.out.println("- Cada inimigo comum derrotado vale 100 pontos.");
        System.out.println("- O chefe final vale 500 pontos.");
        System.out.println("- Você começa com 3 vidas.");
        System.out.println("- Se um inimigo alcançar você, perderá vida.");
        System.out.println("- Para vencer, derrote o chefe na batalha final.");
        System.out.println();
        System.out.println("==================================================");
    }

    public void showTurnHeader(
            String stage,
            int turn,
            int lives,
            int maxLives,
            int score,
            int remainingEnemies,
            Iterable<String> history) {

        System.out.println();
        System.out.println("==================================================");
        System.out.println("                   O JOGO!");
        System.out.println("==================================================");
        System.out.println(stage + " | TURNO " + turn);
        System.out.println("Vidas: " + lives + "/" + maxLives
                + " | Pontos: " + score);
        System.out.println("Inimigos restantes: " + remainingEnemies);
        System.out.println();


    }

    public void showBoard(String[][] board, int rows, int columns) {
        System.out.println("TABULEIRO");
        System.out.print("Posicao ");

        for (int column = 1; column <= columns; column++) {
            System.out.printf("| Pista %-10d", column);
        }

        System.out.println("|");

        for (int row = 0; row < rows; row++) {
            System.out.printf("%7d ", row);

            for (int column = 0; column < columns; column++) {
                System.out.printf("| %-14s", board[row][column]);
            }

            System.out.println("|");
        }

        System.out.println();
    }

    public int askLane() {
        System.out.println("DECISAO DO JOGADOR");
        System.out.println("Escolha a pista que deseja atacar.");

        while (true) {
            System.out.print("Digite 1, 2 ou 3: ");

            if (!scanner.hasNextLine()) {
                return 1;
            }

            String input = scanner.nextLine().trim();

            try {
                int lane = Integer.parseInt(input);

                if (lane >= 1 && lane <= 3) {
                    return lane;
                }
            } catch (NumberFormatException ignored) {
                // Entrada invalida: solicitar novamente.
            }

            System.out.println("Opcao invalida. Escolha 1, 2 ou 3.");
        }
    }

    public void showHistory(Iterable<String> history) {
        System.out.println();
        System.out.println("========== HISTORICO DA PARTIDA ==========");

        boolean hasHistory = false;

        for (String entry : history) {
            System.out.println(" - " + entry);
            hasHistory = true;
        }

        if (!hasHistory) {
            System.out.println("Nenhum acontecimento registrado.");
        }

        System.out.println("==========================================");
    }

    public void showMessage(String message) {
        System.out.println("[JOGO] " + message);
    }

    public void showGameOver(int score) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("                    FIM DE JOGO");
        System.out.println("Pontuacao final: " + score);
        System.out.println("==================================================");
    }

    public void showVictory(int score) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("                VITORIA GLORIOSA!");
        System.out.println("          O chefe final foi derrotado!");
        System.out.println("Pontuacao final: " + score);
        System.out.println("==================================================");
    }
}
