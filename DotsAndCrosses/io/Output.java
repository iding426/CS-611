public class Output {
    public void printWelcome() {
        System.out.println("Welcome to Dots and Crosses!");
    }

    public void printGoodbye() {
        System.out.println("Thanks for playing Dots and Crosses! Goodbye!");
    }

    public void printInvalidMove() {
        System.out.println("Invalid move! Please try again.");
    }

    public void printWin(Player winner) {
        System.out.println("Congratulations " + winner.getUsername() + "! You win!");
    }

    public void printDraw() {
        System.out.println("It's a draw! No more moves possible.");
    }

    public void printBoard(String boardString) {
        System.out.println("Current Board State!");
        System.out.print(boardString);
    }
}