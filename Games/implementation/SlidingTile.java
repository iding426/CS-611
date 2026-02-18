public class SlidingTile extends Tile {
    private int value;

    public SlidingTile(int row, int col, int value) {
        super(row, col);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "_";
        }
        return String.valueOf(value);
    }
}
