package implementation.DotsAndCrosses;

import implementation.Player;

public class DotsAndCrossesPlayer extends Player {
    private int wins;
    private int squaresOwned;

    public DotsAndCrossesPlayer(String username) {
        super(username);
        this.wins = 0;
        this.squaresOwned = 0;
    }

    public int getWins() {
        return wins;
    }

    public void incrementWins() {
        wins++;
    }

    public int getSquaresOwned() {
        return squaresOwned;
    }

    public void setSquaresOwned(int squaresOwned) {
        this.squaresOwned = squaresOwned;
    }

    public void incrementSquaresOwned() {
        squaresOwned++;
    }

    public void resetSquaresOwned() {
        squaresOwned = 0;
    }

    public void displayStats() {
        System.out.println("\n--- " + getUsername() + "'s Stats ---");
        System.out.println("Total Wins: " + wins);
        System.out.println("Squares Owned in Current Game: " + squaresOwned);
    }
}
