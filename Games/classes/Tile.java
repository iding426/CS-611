public abstract class Tile {
    final int rowIndex;
    final int colIndex;
    Player owner;

    public Tile(int row, int col) {
        rowIndex = row;
        colIndex = col;

        owner = null;
    }

    public Player getOwner() {
        return owner;
    }

    public int getRow() {
        return rowIndex;
    }

    public int getColumn() {
        return colIndex;
    }

    public void setOwner(Player p) {
        this.owner = p;
    }
}