package implementation.DotsAndCrosses;

import java.util.HashMap;
import classes.Board;
import classes.Tile;
import implementation.Player;
import io.DotsAndCrossesOutput;

public class DotsCrossesBoard implements Board {
    final int rows;
    final int cols;

    private Tile[][] board;
    private HashMap<Integer, Tile> tileMap;
    private DotsAndCrossesOutput output;

    public DotsCrossesBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.output = new DotsAndCrossesOutput();

        board = new Tile[rows][cols];
        tileMap = new HashMap<>();
        
        // Initialize tiles and map indices 1 to rows*cols
        int index = 1;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                DotsCrossesTile tile = new DotsCrossesTile(row, col);
                board[row][col] = tile;
                tileMap.put(index, tile);
                index++;
            }
        }
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getColumns() {
        return cols;
    }

    @Override
    public Tile getTile(int row, int column) {
        return board[row][column];
    }
    
    // Get tile by index (1 to rows*cols)
    public Tile getTileByIndex(int index) {
        return tileMap.get(index);
    }

    @Override
    public Tile[][] getBoard() {
        return board;
    }

    @Override
    public boolean neighbors(Tile a, Tile b) {
        int rowDiff = Math.abs(a.getRow() - b.getRow());
        int colDiff = Math.abs(a.getColumn() - b.getColumn());

        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    // Mark an edge between two tiles
    public boolean markEdge(DotsCrossesTile a, DotsCrossesTile b, Player p) {
        if (neighbors(a, b)) {
            // Check if tiles were already owned before marking
            boolean aWasOwned = a.getOwner() != null;
            boolean bWasOwned = b.getOwner() != null;
            
            a.setEdgeOwner(b, p);
            b.setEdgeOwner(a, p);
            
            // Check if any tiles became owned after marking
            boolean aIsNowOwned = a.getOwner() != null;
            boolean bIsNowOwned = b.getOwner() != null;
            
            // Return true if at least one box was captured
            return (!aWasOwned && aIsNowOwned) || (!bWasOwned && bIsNowOwned);
        }
        return false;
    }

    public boolean markBorder(DotsCrossesTile tile, String direction, Player p) {
        direction = direction.toLowerCase();
        boolean wasOwned = tile.getOwner() != null;
        
        if (direction.equals("up")) {
            if (tile.getTopEdge() != null) {
                output.printInvalidMove();
                return false;
            }
            if (tile.getRow() == 0) {
                // It's a border edge
                tile.setBorderEdge("up", p);
            }
        } else if (direction.equals("down")) {
            if (tile.getBottomEdge() != null) {
                output.printInvalidMove();
                return false;
            }
            if (tile.getRow() == rows - 1) {
                // It's a border edge
                tile.setBorderEdge("down", p);
            }
        } else if (direction.equals("left")) {
            if (tile.getLeftEdge() != null) {
                output.printInvalidMove();
                return false;
            }
            if (tile.getColumn() == 0) {
                // It's a border edge
                tile.setBorderEdge("left", p);
            }
        } else if (direction.equals("right")) {
            if (tile.getRightEdge() != null) {
                output.printInvalidMove();
                return false;
            }
            if (tile.getColumn() == cols - 1) {
                // It's a border edge
                tile.setBorderEdge("right", p);
            }
        }
        
        // Check if the tile became owned after marking the border
        boolean isNowOwned = tile.getOwner() != null;
        return !wasOwned && isNowOwned;
    }

    public DotsCrossesTile getNeighbor(DotsCrossesTile tile, String direction) {
        int row = tile.getRow();
        int col = tile.getColumn();

        switch (direction.toLowerCase()) {
            case "up":
                return row > 0 ? (DotsCrossesTile) board[row - 1][col] : null;
            case "down":
                return row < rows - 1 ? (DotsCrossesTile) board[row + 1][col] : null;
            case "left":
                return col > 0 ? (DotsCrossesTile) board[row][col - 1] : null;
            case "right":
                return col < cols - 1 ? (DotsCrossesTile) board[row][col + 1] : null;
            default:
                return null;
        }
    }

    public boolean boardFilled() {
        boolean filled = true;

        for (Tile[] row : board) {
            for (Tile tile : row) {
                if (tile.getOwner() == null) {
                    filled = false;
                    break;
                }
            }
            if (!filled) {
                break;
            }
        }

        return filled;
    }

    public Player getWinner() {
        HashMap<Player, Integer> playerScores = new HashMap<>();

        for (Tile[] row : board) {
            for (Tile tile : row) {
                Player owner = tile.getOwner();
                if (owner != null) {
                    playerScores.put(owner, playerScores.getOrDefault(owner, 0) + 1);
                }
            }
        }

        Player winner = null;
        int maxScore = -1;
        int playersWithMaxScore = 0;

        for (Player p : playerScores.keySet()) {
            int score = playerScores.get(p);
            if (score > maxScore) {
                maxScore = score;
                winner = p;
                playersWithMaxScore = 1;
            } else if (score == maxScore) {
                playersWithMaxScore++;
            }
        }

        // Return null if there's a tie (draw)
        if (playersWithMaxScore > 1) {
            return null;
        }

        return winner;
    }

}