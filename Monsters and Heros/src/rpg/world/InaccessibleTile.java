package rpg.world;

public class InaccessibleTile extends Tile {
    public InaccessibleTile(Position position) {
        super(position);
    }

    @Override
    public TileType getType() {
        return TileType.INACCESSIBLE;
    }

    @Override
    public boolean isAccessible() {
        return false;
    }
}
