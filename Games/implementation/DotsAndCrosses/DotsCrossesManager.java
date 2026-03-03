package implementation.DotsAndCrosses;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import classes.GameManager;
import classes.Tile;
import implementation.Player;
import io.DotsAndCrossesInput;
import io.DotsAndCrossesOutput;
import io.QuitGameException;

public class DotsCrossesManager extends GameManager {
    private DotsCrossesBoard board;
    private DotsAndCrossesPlayer player1;
    private DotsAndCrossesPlayer player2;
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
        player1 = new DotsAndCrossesPlayer(name1);
        
        System.out.println("\nPlayer 2");
        String name2 = input.getUsername();
        player2 = new DotsAndCrossesPlayer(name2);
    }

    // Allow replayability 
    public void playWithReplay() {
        boolean keepPlaying = true;
        while (keepPlaying) {
            try {
                initGame();
                gameLoop();
                
                // Display final stats
                displayFinalStats();
                
                int choice = input.getReplayChoice();
                switch (choice) {
                    case 1: // Replay with same players
                        System.out.println("\nStarting new game with same players...");
                        resetForNewGame();
                        break;
                    case 2: // Return to main menu
                        keepPlaying = false;
                        break;
                    case 3: // Exit
                        System.out.println("Thanks for playing!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Returning to main menu.");
                        keepPlaying = false;
                }
            } catch (QuitGameException e) {
                System.out.println("\nQuitting game...");
                keepPlaying = false;
            }
        }
    }

    // Reset state
    private void resetForNewGame() {
        board = null;
        player1.resetSquaresOwned();
        player2.resetSquaresOwned();
        p1Turn = true;
    }

    private void displayFinalStats() {
        System.out.println("\n========== Final Statistics ==========");
        player1.displayStats();
        player2.displayStats();
        System.out.println("======================================");
    }

    // Count how many squares each player owns
    private void updateSquaresOwned() {
        player1.resetSquaresOwned();
        player2.resetSquaresOwned();
        
        Tile[][] boardGrid = board.getBoard();
        for (Tile[] row : boardGrid) {
            for (Tile tile : row) {
                Player owner = tile.getOwner();
                if (owner != null) {
                    if (owner.equals(player1)) {
                        player1.incrementSquaresOwned();
                    } else if (owner.equals(player2)) {
                        player2.incrementSquaresOwned();
                    }
                }
            }
        }
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
            // Update squares owned count
            updateSquaresOwned();
            
            if (gameEnd()) {
                Player winner = getWinner();
                if (winner != null) {
                    output.printWin(winner);
                    if (winner.equals(player1)) {
                        player1.incrementWins();
                    } else {
                        player2.incrementWins();
                    }
                } else {
                    output.printDraw();
                }
                // Final update of squares owned
                updateSquaresOwned();
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

            boolean boxCaptured = false;
            if (tile2 != null) {
                if (tile1.getOwner() != null || tile2.getOwner() != null) {
                    output.printInvalidMove();
                    continue;
                } else {
                    boxCaptured = board.markEdge(tile1, tile2, p);
                }
            } else {
                // Border
                boxCaptured = board.markBorder(tile1, direction.toLowerCase(), p);
            }

            output.printBoard(getBoardString());
            
            // Only switch turns if no box was captured
            if (!boxCaptured) {
                p1Turn = !p1Turn;
            } else {
                System.out.println(p.getUsername() + " captured a box! Take another turn.");
            }
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