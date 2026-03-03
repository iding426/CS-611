package implementation.Quoridor;

import implementation.Player;

public class QuoridorPlayer extends Player {
    private int wallsRemaining;
    private int currentRow;
    private int currentCol;
    private int goalRow;
    private boolean isPlayerOne;
    private int wins;

    public QuoridorPlayer(String username, boolean isPlayerOne) {
        super(username);
        this.isPlayerOne = isPlayerOne;
        this.wallsRemaining = 10; // Each player starts with 10 walls
        this.wins = 0;
        
        // Player 1 starts at top (row 0), Player 2 starts at bottom (row 8)
        if (isPlayerOne) {
            this.currentRow = 0;
            this.currentCol = 4; // Middle of the board
            this.goalRow = 8; // Need to reach bottom
        } else {
            this.currentRow = 8;
            this.currentCol = 4;
            this.goalRow = 0; // Need to reach top
        }
    }

    public int getWallsRemaining() {
        return wallsRemaining;
    }

    public void decrementWalls() {
        if (wallsRemaining > 0) {
            wallsRemaining--;
        }
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public int getCurrentCol() {
        return currentCol;
    }

    public void setPosition(int row, int col) {
        this.currentRow = row;
        this.currentCol = col;
    }

    public int getGoalRow() {
        return goalRow;
    }

    public boolean isPlayerOne() {
        return isPlayerOne;
    }

    public boolean hasReachedGoal() {
        return currentRow == goalRow;
    }

    public int getWins() {
        return wins;
    }

    public void incrementWins() {
        wins++;
    }

    public void resetForNewGame() {
        wallsRemaining = 10;
        resetMoveCount();
        
        if (isPlayerOne) {
            currentRow = 0;
            currentCol = 4;
        } else {
            currentRow = 8;
            currentCol = 4;
        }
    }

    public void displayStats() {
        System.out.println("\n--- " + getUsername() + "'s Stats ---");
        System.out.println("Total Wins: " + wins);
        System.out.println("Moves Made in Current Game: " + getMoveCount());
        System.out.println("Walls Remaining: " + wallsRemaining);
    }
}
