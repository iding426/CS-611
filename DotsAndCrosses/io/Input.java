import java.util.Scanner;

public class Input {
    private Scanner scanner;

    public Input() {
        scanner = new Scanner(System.in);
    }

    public String getUsername() {
        System.out.print("Enter your username: ");
        return scanner.nextLine();
    }

    public int[] getPuzzleSize() {
        System.out.print("Enter the number of rows you want to play with: ");
        int rows = scanner.nextInt();
        System.out.print("Enter the number of columns you want to play with: ");
        int cols = scanner.nextInt();

        return new int[]{rows, cols};
    }

    public int[] getMove() {
        System.out.println("Now enter the tiles the edge you want to mark is between!");
        System.out.print("Enter the index of the first tile: ");
        int tile1 = scanner.nextInt();
        System.out.print("Enter the index of the second tile: ");
        int tile2 = scanner.nextInt();

        return new int[]{tile1, tile2};
    }
}