package rpg.world;

public class CommonTile extends Tile {
    public CommonTile(Position position) {
        super(position);
    }

    @Override
    public TileType getType() {
        return TileType.COMMON;
    }

    @Override
    public boolean isAccessible() {
        return true;
    }

    @Override
    public boolean canTriggerBattle() {
        return true;
    }
}
