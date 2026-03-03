package implementation.Quoridor;

import classes.Tile;

public class QuoridorTile extends Tile {
    private boolean hasPlayer1;
    private boolean hasPlayer2;

    public QuoridorTile(int row, int col) {
        super(row, col);
        hasPlayer1 = false;
        hasPlayer2 = false;
    }

    public boolean hasPlayer1() {
        return hasPlayer1;
    }

    public boolean hasPlayer2() {
        return hasPlayer2;
    }

    public void setPlayer1(boolean present) {
        hasPlayer1 = present;
    }

    public void setPlayer2(boolean present) {
        hasPlayer2 = present;
    }

    public boolean isEmpty() {
        return !hasPlayer1 && !hasPlayer2;
    }
}
