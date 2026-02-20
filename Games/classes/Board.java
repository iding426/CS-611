package classes;

public interface Board {
    public abstract int getRows();
    public abstract int getColumns();

    public abstract Tile getTile(int row, int column);
    public abstract Tile[][] getBoard();

    public abstract boolean neighbors(Tile a, Tile b);
}