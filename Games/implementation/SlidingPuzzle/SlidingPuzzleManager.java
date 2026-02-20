package implementation.SlidingPuzzle;

import classes.GameManager;
import implementation.Player;
import io.SlidingInput;
import io.SlidingOutput;

/*
    TODO:
        - Fill in Extended Methods
*/

public class SlidingPuzzleManager extends GameManager {
    private SlidingBoard board;
    private SlidingInput input;
    private SlidingOutput output;
    private boolean puzzleSolved;
    private SlidingPlayer player;

    public SlidingPuzzleManager() {
        input = new SlidingInput();
        output = new SlidingOutput();
        puzzleSolved = false;
        
        System.out.println("\nEnter player name:");
        String name = input.getUsername();
        player = new SlidingPlayer(name);
    }

    // High Level Control
    public void start() {
        boolean keepPlaying = true;
        while (keepPlaying) {
            initGame();
            gameLoop();
            
            // Display final stats
            displayFinalStats();
            
            int choice = input.getReplayChoice();
            switch (choice) {
                case 1: // Play again
                    System.out.println("\nStarting new game...");
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
        }
    }

    private void resetForNewGame() {
        puzzleSolved = false;
        player.resetCurrentGameMoves();
        board = null;
    }

    private void displayFinalStats() {
        System.out.println("\n========== Final Statistics ==========");
        player.displayStats();
        System.out.println("======================================");
    }

    // GameManager abstract methods
    @Override
    public void initGame() {
        printWelcome();
        setupGame();
    }

    @Override
    public boolean gameEnd() {
        return puzzleSolved;
    }

    @Override
    public Player getWinner() {
        // Sliding puzzle is a single-player game, so no winner concept
        return null;
    }

    // Welcome
    private void printWelcome() {
        output.printWelcome();
    }

    // Setup
    private void setupGame() {
        int rows = input.getDimension("rows");
        int cols = input.getDimension("columns");
        board = new SlidingBoard(rows, cols);
        
        output.printBoardCreated();
        board.printBoard();

        output.printShuffling();
        board.shuffle();
    }



    // Loop to play the game
    @Override
    public void gameLoop() {
        while (!gameEnd()) {
            board.printBoard();

            if (board.isSolved()) {
                output.printPuzzleSolved();
                System.out.println("Congratulations " + player.getUsername() + "! You solved the puzzle in " + player.getCurrentGameMoves() + " moves!");
                puzzleSolved = true;
                player.incrementWins();
                break;
            }

            int tileNumber = input.getTileToSlide();

            // User wants to quit
            if (tileNumber == -1) {
                output.printGoodbye();
                break;
            }

            // Check if tile exists
            if (board.getTilePosition(tileNumber) == null) {
                output.printTileNotFound();
                continue;
            }

            // Try to move the tile
            if (board.slideTile(tileNumber)) {
                moveCount++;
                player.incrementCurrentGameMoves();
            } else {
                output.printInvalidMove();
            }
        }
    }
}