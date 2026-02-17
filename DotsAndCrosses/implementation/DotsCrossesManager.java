public class DotsCrossesManager extends GameManager {
    private DotsCrossesBoard board;
    private Player player1;
    private Player player2;
    private Input input;
    private Output output;

    public DotsCrossesManager() {
        board = null;
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");

        input = new Input();
        output = new Output();
    }

    private void initBoard(int rows, int cols) {
        board = new DotsCrossesBoard(rows, cols);
    }

    @Override
    public void initGame() {
        output.printWelcome();

        // Get player names
        System.out.println("\nPlayer 1:");
        String name1 = input.getUsername();
        player1 = new Player(name1);
        
        System.out.println("\nPlayer 2:");
        String name2 = input.getUsername();
        player2 = new Player(name2);

        // Get board size
        int[] size = input.getPuzzleSize();
        initBoard(size[0], size[1]);
        
        // Display initial board
        System.out.println("\nStarting board:");
        output.printBoard(getBoardString());
        System.out.println(player1.getUsername() + " = X, " + player2.getUsername() + " = O");
        
        System.out.println("\nHow to play:");
        System.out.println("- Take turns selecting edges between tiles");
        System.out.println("- Complete all 4 edges of a square to claim it");
        System.out.println("- The player with the most squares wins!");
        System.out.println();
    }

    @Override
    public void gameLoop() {

    }

    @Override
    public boolean gameEnd() {
        return board.boardFilled();
    }

    @Override
    public Player getWinner() {
        return board.getWinner();
    }

    public String getBoardString() {
        StringBuilder sb = new StringBuilder();
        Tile[][] boardGrid = board.getBoard();
        int rows = board.getRows();
        int cols = board.getColumns();
        
        for (int row = 0; row < rows; row++) {
            // Print dots and horizontal edges
            for (int col = 0; col < cols; col++) {
                sb.append("o");
                
                DotsCrossesTile tile = (DotsCrossesTile) boardGrid[row][col];
                // Horizontal edge (top)
                if (tile.getTopEdge() != null) {
                    sb.append("───");
                } else {
                    sb.append("   ");
                }
            }
            sb.append("o\n");
            
            // Print vertical edges and tile content
            for (int col = 0; col < cols; col++) {
                DotsCrossesTile tile = (DotsCrossesTile) boardGrid[row][col];
                
                // Vertical edge (left)
                if (tile.getLeftEdge() != null) {
                    sb.append("│");
                } else {
                    sb.append(" ");
                }
                
                // Tile content
                int tileIndex = row * cols + col + 1;
                Player owner = tile.getOwner();
                if (owner != null) {
                    // Use X for player1, O for player2
                    String symbol = owner.equals(player1) ? "X" : "O";
                    sb.append(String.format(" %s ", symbol));
                } else {
                    sb.append(String.format("%2d ", tileIndex));
                }
            }
            
            // Right edge of last column
            DotsCrossesTile lastTile = (DotsCrossesTile) boardGrid[row][cols - 1];
            if (lastTile.getRightEdge() != null) {
                sb.append("│");
            } else {
                sb.append(" ");
            }
            sb.append("\n");
        }
        
        // Print bottom row of dots and horizontal edges
        for (int col = 0; col < cols; col++) {
            sb.append("o");
            
            DotsCrossesTile tile = (DotsCrossesTile) boardGrid[rows - 1][col];
            // Bottom edge
            if (tile.getBottomEdge() != null) {
                sb.append("───");
            } else {
                sb.append("   ");
            }
        }
        sb.append("o\n");
        
        return sb.toString();
    }
}