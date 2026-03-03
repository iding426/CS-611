package io;

import implementation.Player;

public class QuoridorOutput extends Output {
    @Override
    public void printWelcome() {
        System.out.println("\n=================================");
        System.out.println("      Welcome to Quoridor!       ");
        System.out.println("=================================");
    }

    @Override
    public void printGoodbye() {
        System.out.println("Thanks for playing Quoridor! Goodbye!");
    }

    public void printGameRules() {
        System.out.println("\n--- How to Play Quoridor ---");
        System.out.println("Goal: Be the first player to reach the opposite side of the board.");
        System.out.println("\nOn your turn, you can either:");
        System.out.println("1. Move your pawn one square (up, down, left, or right)");
        System.out.println("2. Place a wall to block your opponent (each player has 10 walls)");
        System.out.println("\nWalls:");
        System.out.println("- Walls are 2 squares long and can be horizontal (H) or vertical (V)");
        System.out.println("- Walls cannot completely block a player from reaching their goal");
        System.out.println("- Walls are represented by '═' (horizontal) and '║' (vertical)");
        System.out.println("\nNote: Type 'quit' or 'q' at any time to exit the game.");
        System.out.println();
    }

    public void printBoard(String boardString) {
        System.out.println("\n" + boardString);
    }

    public void nextMove(Player p, int wallsRemaining) {
        System.out.println("\n" + p.getUsername() + "'s turn! (Walls remaining: " + wallsRemaining + ")");
    }

    public void printInvalidMove() {
        System.out.println("Invalid move! Please try again.");
    }

    public void printWin(Player winner) {
        System.out.println("\n=================================");
        System.out.println("Congratulations " + winner.getUsername() + "! You win!");
        System.out.println("=================================");
    }

    public void printInvalidWallPlacement(String reason) {
        System.out.println("Invalid wall placement: " + reason);
    }

    public void printNoWallsRemaining() {
        System.out.println("You have no walls remaining! Please move your pawn instead.");
    }
}
