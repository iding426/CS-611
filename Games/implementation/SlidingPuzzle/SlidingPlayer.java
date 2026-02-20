package implementation.SlidingPuzzle;

import implementation.Player;

public class SlidingPlayer extends Player {
    private int wins;
    private int currentGameMoves;

    public SlidingPlayer(String username) {
        super(username);
        this.wins = 0;
        this.currentGameMoves = 0;
    }

    public int getWins() {
        return wins;
    }

    public void incrementWins() {
        wins++;
    }

    public int getCurrentGameMoves() {
        return currentGameMoves;
    }

    public void incrementCurrentGameMoves() {
        currentGameMoves++;
    }

    public void resetCurrentGameMoves() {
        currentGameMoves = 0;
    }

    public void displayStats() {
        System.out.println("\n--- " + getUsername() + "'s Stats ---");
        System.out.println("Total Wins: " + wins);
        System.out.println("Moves in Current Game: " + currentGameMoves);
    }
}
