/*
    TODO:
        - Fill in Extended Methods
*/

public class SlidingPuzzleManager extends GameManager {
    private SlidingBoard board;
    private SlidingInput input;
    private SlidingOutput output;
    private boolean puzzleSolved;

    public SlidingPuzzleManager() {
        input = new SlidingInput();
        output = new SlidingOutput();
        puzzleSolved = false;
    }

    // High Level Control
    public void start() {
        initGame();
        gameLoop();
    }

    // GameManager abstract methods
    void initGame() {
        printWelcome();
        setupGame();
    }

    boolean gameEnd() {
        return puzzleSolved;
    }

    Player getWinner() {
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
    void gameLoop() {
        while (!gameEnd()) {
            board.printBoard();

            if (board.isSolved()) {
                output.printPuzzleSolved();
                puzzleSolved = true;
                break;
            }

            int r = input.getRowToSlide();

            // User wants to quit
            if (r == -1) {
                output.printGoodbye();
                break;
            }

            int c = input.getColumnToSlide();

            boolean valid = false;
            for (int[] move : board.availableMoves()) {
                if (move[0] == r && move[1] == c) {
                    valid = true;
                    break;
                }
            }

            if (valid) {
                board.slide(r, c);
                moveCount++;
            } else {
                output.printInvalidMove();
            }
        }
    }
}