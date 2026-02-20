package io;

import implementation.SlidingPuzzle.SlidingTile;

public class SlidingOutput extends Output {
    @Override
    public void printWelcome() {
        System.out.println("Welcome to the Sliding Puzzle Game!");
        System.out.println("Try to order the tiles from least to greatest.");
    }

    @Override
    public void printGoodbye() {
        System.out.println("Goodbye!");
    }

    public void printBoardCreated() {
        System.out.println("Board Created!");
    }

    public void printShuffling() {
        System.out.println("Shuffling!");
    }

    public void printBoard(SlidingTile[][] grid, int rows, int cols) {
        // Print top border
        for (int j = 0; j < cols; j++) {
            System.out.print("+---");
        }
        System.out.println("+");
        
        // Print each row
        for (int i = 0; i < rows; i++) {
            // Print cell values
            for (int j = 0; j < cols; j++) {
                System.out.print("| ");
                System.out.print(grid[i][j].toString());
                System.out.print(" ");
            }
            System.out.println("|");
            
            // Print horizontal border after each row
            for (int j = 0; j < cols; j++) {
                System.out.print("+---");
            }
            System.out.println("+");
        }
    }

    public void printPuzzleSolved() {
        System.out.println("Puzzle solved!");
    }

    public void printInvalidMove() {
        System.out.println("Invalid move. That tile cannot be moved.");
    }

    public void printTileNotFound() {
        System.out.println("That tile number does not exist on the board.");
    }
}
