package rpg.world;

public abstract class Tile {
    private final Position position;

    protected Tile(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public abstract TileType getType();

    public abstract boolean isAccessible();

    public boolean isMarket() {
        return false;
    }

    public boolean canTriggerBattle() {
        return false;
    }

    public char renderSymbol() {
        return switch (getType()) {
            case INACCESSIBLE -> 'X';
            case MARKET -> 'M';
            case COMMON -> 'C';
        };
    }
}
