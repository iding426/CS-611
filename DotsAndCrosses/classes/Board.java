abstract class Board {
    int rows;
    int cols;
    Tile[][] board;

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Tile[][] getBoard() {
        return board;
    }

    public Tile getTile(int row, int col) {
        return board[row][col];
    }

    public abstract boolean neighbors(Tile a, Tile b);
}