package rpg.world;

public class MarketTile extends Tile {
    public MarketTile(Position position) {
        super(position);
    }

    @Override
    public TileType getType() {
        return TileType.MARKET;
    }

    @Override
    public boolean isAccessible() {
        return true;
    }

    @Override
    public boolean isMarket() {
        return true;
    }
}
