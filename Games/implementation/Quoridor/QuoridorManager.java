package implementation.Quoridor;

import classes.GameManager;
import implementation.Player;
import io.QuoridorInput;
import io.QuoridorOutput;
import io.QuitGameException;

public class QuoridorManager extends GameManager {
    private QuoridorBoard board;
    private QuoridorPlayer player1;
    private QuoridorPlayer player2;
    private QuoridorInput input;
    private QuoridorOutput output;
    private boolean p1Turn;

    public QuoridorManager() {
        input = new QuoridorInput();
        output = new QuoridorOutput();

        System.out.println("\nPlayer 1 (starts at top, goal: reach bottom)");
        String name1 = input.getUsername();
        player1 = new QuoridorPlayer(name1, true);

        System.out.println("\nPlayer 2 (starts at bottom, goal: reach top)");
        String name2 = input.getUsername();
        player2 = new QuoridorPlayer(name2, false);
    }

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

    private void resetForNewGame() {
        board = null;
        player1.resetForNewGame();
        player2.resetForNewGame();
        p1Turn = true;
        moveCount = 0;
    }

    private void displayFinalStats() {
        System.out.println("\n========== Final Statistics ==========");
        player1.displayStats();
        player2.displayStats();
        System.out.println("======================================");
    }

    @Override
    public void initGame() {
        board = new QuoridorBoard();
        p1Turn = true;

        output.printWelcome();
        output.printGameRules();

        System.out.println("Starting positions:");
        System.out.println(player1.getUsername() + " (Player 1) at row 0, column 4");
        System.out.println(player2.getUsername() + " (Player 2) at row 8, column 4");

        output.printBoard(board.getBoardString(player1, player2));
    }

    @Override
    public void gameLoop() {
        while (!gameEnd()) {
            QuoridorPlayer currentPlayer = p1Turn ? player1 : player2;
            QuoridorPlayer opponent = p1Turn ? player2 : player1;

            output.nextMove(currentPlayer, currentPlayer.getWallsRemaining());
            output.printBoard(board.getBoardString(player1, player2));

            boolean validMove = false;
            while (!validMove) {
                int choice = input.getMoveChoice();

                if (choice == 1) {
                    // Move pawn
                    validMove = handlePawnMove(currentPlayer, opponent);
                } else if (choice == 2) {
                    // Place wall
                    if (currentPlayer.getWallsRemaining() == 0) {
                        output.printNoWallsRemaining();
                        continue;
                    }
                    validMove = handleWallPlacement(currentPlayer);
                } else {
                    output.printInvalidMove();
                }
            }

            currentPlayer.incrementMoveCount();
            moveCount++;
            p1Turn = !p1Turn;
        }

        // Game ended, announce winner
        Player winner = getWinner();
        if (winner != null) {
            output.printWin(winner);
            if (winner == player1) {
                player1.incrementWins();
            } else {
                player2.incrementWins();
            }
            output.printBoard(board.getBoardString(player1, player2));
        }
    }

    private boolean handlePawnMove(QuoridorPlayer currentPlayer, QuoridorPlayer opponent) {
        int[] move = input.getPawnMove();
        int toRow = move[0];
        int toCol = move[1];

        if (board.isValidPawnMove(currentPlayer.getCurrentRow(), currentPlayer.getCurrentCol(),
                toRow, toCol, opponent.getCurrentRow(), opponent.getCurrentCol())) {
            board.movePawn(currentPlayer, toRow, toCol);
            return true;
        } else {
            output.printInvalidMove();
            return false;
        }
    }

    private boolean handleWallPlacement(QuoridorPlayer currentPlayer) {
        Object[] wallData = input.getWallPlacement();
        int row = (int) wallData[0];
        int col = (int) wallData[1];
        String orientation = (String) wallData[2];

        if (!orientation.equals("H") && !orientation.equals("V")) {
            output.printInvalidWallPlacement("Orientation must be H or V");
            return false;
        }

        if (board.placeWall(row, col, orientation, player1, player2)) {
            currentPlayer.decrementWalls();
            return true;
        } else {
            output.printInvalidWallPlacement("Wall placement would block a player's path or overlaps with existing walls");
            return false;
        }
    }

    @Override
    public boolean gameEnd() {
        return player1.hasReachedGoal() || player2.hasReachedGoal();
    }

    @Override
    public Player getWinner() {
        if (player1.hasReachedGoal()) {
            return player1;
        } else if (player2.hasReachedGoal()) {
            return player2;
        }
        return null;
    }
}
