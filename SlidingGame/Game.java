import java.util.Scanner;

public class Game {
    private Board board;
    private Scanner scanner;

    public Game() {
        scanner = new Scanner(System.in);
    }

    // High Level Control
    public void start() {
        printWelcome();
        setupGame();
        gameLoop();
    }

    // Welcome
    private void printWelcome() {
        System.out.println("Welcome to the Sliding Puzzle Game!");
        System.out.println("Try to order the tiles from least to greatest.");
    }

    // Setup
    private void setupGame() {
        int rows = promptForDimension("rows");
        int cols = promptForDimension("columns");
        board = new Board(rows, cols);
        
        System.out.println("Board Created!");
        board.printBoard();

        System.out.println("Shuffling!");
        board.shuffle();
    }

    // Get each dimension
    private int promptForDimension(String dimensionName) {
        int value = 0;
        while (value <= 0) {
            System.out.print("Enter number of " + dimensionName + ": ");
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                if (value <= 1) {
                    System.out.println("Value must be at least 2.");
                }
            } else {
                System.out.println("Please enter a valid integer.");
                scanner.next(); // discard invalid input
            }
        }
        return value;
    }

    // Loop to play the game
    private void gameLoop() {
        while (true) {
            board.printBoard();

            if (board.isSolved()) {
                System.out.println("Puzzle solved!");
                break;
            }

            System.out.println("Enter the row and column of the tile to slide (or -1 to quit):");

            int r = scanner.nextInt(); 

            // User wants to quit
            if (r == -1) {
                System.out.println("Goodbye!");
                break;
            }

            int c = scanner.nextInt();

            boolean valid = false;
            for (int[] move : board.availableMoves()) {
                if (move[0] == r && move[1] == c) {
                    valid = true;
                    break;
                }
            }

            if (valid) {
                board.slide(r, c);
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }
    }
}