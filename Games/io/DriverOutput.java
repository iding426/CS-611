public class DriverOutput extends Output {
    @Override
    public void printWelcome() {
        System.out.println("=================================");
        System.out.println("   Welcome to the Game Suite!   ");
        System.out.println("=================================");
    }

    @Override
    public void printGoodbye() {
        System.out.println("\nThank you for playing!");
        System.out.println("Goodbye!");
    }

    public void printGameMenu() {
        System.out.println("\nPlease select a game to play:");
        System.out.println("1. Dots and Crosses");
        System.out.println("2. Sliding Puzzle");
        System.out.println("3. Exit");
    }

    public void printInvalidChoice() {
        System.out.println("Invalid choice. Please select a valid option.");
    }

    public void printSeparator() {
        System.out.println("\n=================================\n");
    }
}
