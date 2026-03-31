package rpg.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import rpg.encounter.Encounter;
import rpg.encounter.EncounterGenerator;
import rpg.util.Dice;

public class WorldMap {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    private final WorldConfig config;
    private final Tile[][] grid;
    private final Dice dice;
    private Position partyPosition;

    public WorldMap(WorldConfig config, Dice dice) {
        this.config = config;
        this.dice = dice;
        this.grid = buildGrid(config.size());
        this.partyPosition = pickStartingPosition();
    }

    public MoveResult moveParty(Direction direction) {
        Position target = partyPosition.move(direction);
        if (!isWithinBounds(target)) {
            return MoveResult.BLOCKED_BY_BOUNDARY;
        }
        Tile targetTile = tileAt(target);
        if (!targetTile.isAccessible()) {
            return MoveResult.BLOCKED_BY_INACCESSIBLE;
        }
        partyPosition = target;
        return MoveResult.MOVED;
    }

    public boolean isPartyOnMarket() {
        return tileAt(partyPosition).isMarket();
    }

    public Optional<Encounter> tryTriggerEncounter(
            int heroLevel,
            int partySize,
            EncounterGenerator encounterGenerator) {
        Tile tile = tileAt(partyPosition);
        if (!tile.canTriggerBattle()) {
            return Optional.empty();
        }

        // Encounter chance is checked only on common tiles.
        int roll = dice.roll(100);
        int threshold = (int) Math.round(config.encounterChance() * 100);
        if (roll <= threshold) {
            return Optional.of(encounterGenerator.generateEncounter(heroLevel, partySize));
        }
        return Optional.empty();
    }

    public Position getPartyPosition() {
        return partyPosition;
    }

    public TileType getCurrentTileType() {
        return tileAt(partyPosition).getType();
    }

    public String renderMap() {
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < config.size(); row++) {
            for (int col = 0; col < config.size(); col++) {
                Position current = new Position(row, col);
                if (current.equals(partyPosition)) {
                    // Current party position always wins visually over tile type colors.
                    builder.append(colorize('P', ANSI_GREEN));
                } else {
                    Tile tile = grid[row][col];
                    char symbol = tile.renderSymbol();
                    builder.append(colorizeByType(symbol, tile.getType()));
                }
                builder.append(' ');
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    private String colorizeByType(char symbol, TileType type) {
        return switch (type) {
            case INACCESSIBLE -> colorize(symbol, ANSI_RED);
            case MARKET -> colorize(symbol, ANSI_YELLOW);
            case COMMON -> String.valueOf(symbol);
        };
    }

    private String colorize(char symbol, String color) {
        return color + symbol + ANSI_RESET;
    }

    private Tile[][] buildGrid(int size) {
        List<TileType> tileTypes = createShuffledTileTypes(size * size);
        Tile[][] result = new Tile[size][size];

        int index = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Position position = new Position(row, col);
                TileType type = tileTypes.get(index++);
                result[row][col] = tileFromType(type, position);
            }
        }
        return result;
    }

    private List<TileType> createShuffledTileTypes(int totalTiles) {
        int inaccessibleCount = (int) Math.round(totalTiles * config.inaccessibleRatio());
        int marketCount = (int) Math.round(totalTiles * config.marketRatio());
        int commonCount = totalTiles - inaccessibleCount - marketCount;

        List<TileType> types = new ArrayList<>(totalTiles);
        for (int i = 0; i < inaccessibleCount; i++) {
            types.add(TileType.INACCESSIBLE);
        }
        for (int i = 0; i < marketCount; i++) {
            types.add(TileType.MARKET);
        }
        for (int i = 0; i < commonCount; i++) {
            types.add(TileType.COMMON);
        }
        Collections.shuffle(types);
        return types;
    }

    private Position pickStartingPosition() {
        int preferredRow = config.size() - 1;
        int preferredCol = config.size() / 2;
        Position preferred = new Position(preferredRow, preferredCol);
        if (tileAt(preferred).isAccessible()) {
            return preferred;
        }

        for (int row = config.size() - 1; row >= 0; row--) {
            for (int col = 0; col < config.size(); col++) {
                Position candidate = new Position(row, col);
                if (tileAt(candidate).isAccessible()) {
                    return candidate;
                }
            }
        }
        throw new IllegalStateException("Map has no accessible starting tile");
    }

    private Tile tileFromType(TileType type, Position position) {
        return switch (type) {
            case INACCESSIBLE -> new InaccessibleTile(position);
            case MARKET -> new MarketTile(position);
            case COMMON -> new CommonTile(position);
        };
    }

    private boolean isWithinBounds(Position position) {
        int row = position.row();
        int col = position.col();
        return row >= 0 && row < config.size() && col >= 0 && col < config.size();
    }

    private Tile tileAt(Position position) {
        return grid[position.row()][position.col()];
    }
}
