/*
    TODO:
        - Win statistics
        - Print final stats
*/
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DotsCrossesManager extends GameManager {
    private DotsCrossesBoard board;
    private Player player1;
    private Player player2;
    private DotsAndCrossesInput input;
    private DotsAndCrossesOutput output;

    private boolean p1Turn;
    private final Set<String> directions = new HashSet<>(Arrays.asList("Up", "Down", "Left", "Right"));

    public DotsCrossesManager() {
        board = null;
        player1 = null;
        player2 = null;
        p1Turn = true;

        input = new DotsAndCrossesInput();
        output = new DotsAndCrossesOutput();

        System.out.println("\nPlayer 1");
        String name1 = input.getUsername();
        player1 = new Player(name1);
        
        System.out.println("\nPlayer 2");
        String name2 = input.getUsername();
        player2 = new Player(name2);
    }

    private void initBoard(int rows, int cols) {
        board = new DotsCrossesBoard(rows, cols);
    }

    @Override
    public void initGame() {
        output.printWelcome();

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
        while (true) {
            if (gameEnd()) {
                Player winner = getWinner();
                if (winner != null) {
                    output.printWin(winner);
                } else {
                    output.printDraw();
                }
                break;
            }

            Player p = p1Turn ? player1 : player2;

            output.nextMove(p);
            Object[] move = input.getMove();
            int tileIndex = (int) move[0];
            String direction = (String) move[1];
            
            DotsCrossesTile tile1 = (DotsCrossesTile) board.getTileByIndex(tileIndex);

            if (!directions.contains(direction)) {
                output.printInvalidMove();
                continue;
            }

            // Make sure edge isn't owned
            if (direction == "Left") {
                if (tile1.getLeftEdge() != null) {
                    output.printInvalidMove();
                    continue;
                }
            } else if (direction == "Right") {
                if (tile1.getRightEdge() != null) {
                    output.printInvalidMove();
                    continue;
                }
            } else if (direction == "Up") {
                if (tile1.getTopEdge() != null) {
                    output.printInvalidMove();
                    continue;
                }
            } else if (direction == "Down") {
                if (tile1.getBottomEdge() != null) {
                    output.printInvalidMove();
                    continue;
                }
            }

            DotsCrossesTile tile2 = board.getNeighbor(tile1, direction.toLowerCase());

            if (tile2 != null) {
                if (tile1.getOwner() != null || tile2.getOwner() != null) {
                    output.printInvalidMove();
                    continue;
                } else {
                    
                    board.markEdge(tile1, tile2, p);

                    p1Turn = !p1Turn;
                }
            } else {
                // Border
                board.markBorder(tile1, direction.toLowerCase(), p);
            }

            output.printBoard(getBoardString());
        }
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
                sb.append("+");
                
                DotsCrossesTile tile = (DotsCrossesTile) boardGrid[row][col];
                // Horizontal edge (top)
                if (tile.getTopEdge() != null) {
                    sb.append("---");
                } else {
                    sb.append("   ");
                }
            }
            sb.append("+\n");
            
            // Print vertical edges and tile content
            for (int col = 0; col < cols; col++) {
                DotsCrossesTile tile = (DotsCrossesTile) boardGrid[row][col];
                
                // Vertical edge (left)
                if (tile.getLeftEdge() != null) {
                    sb.append("|");
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
                sb.append("|");
            } else {
                sb.append(" ");
            }
            sb.append("\n");
        }
        
        // Print bottom row of dots and horizontal edges
        for (int col = 0; col < cols; col++) {
            sb.append("+");
            
            DotsCrossesTile tile = (DotsCrossesTile) boardGrid[rows - 1][col];
            // Bottom edge
            if (tile.getBottomEdge() != null) {
                sb.append("---");
            } else {
                sb.append("   ");
            }
        }
        sb.append("+\n");
        
        return sb.toString();
    }
}