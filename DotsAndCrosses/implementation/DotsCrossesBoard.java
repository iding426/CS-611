import java.util.HashMap;

public class DotsCrossesBoard implements Board {
    final int rows;
    final int cols;

    private Tile[][] board;
    private HashMap<Integer, Tile> tileMap;

    public DotsCrossesBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

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
    public void markEdge(DotsCrossesTile a, DotsCrossesTile b, Player p) {
        if (neighbors(a, b)) {
            a.setEdgeOwner(b, p);
            b.setEdgeOwner(a, p);
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