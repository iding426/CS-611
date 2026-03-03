package io;

public class DotsAndCrossesInput extends Input {
    public DotsAndCrossesInput() {
        super();
    }

    @Override
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

    public Object[] getMove() {
        System.out.println("Now enter the edge you want to select (or 'quit' to exit)!");
        System.out.print("Enter the index of the tile: ");
        while (!scanner.hasNextInt()) {
            String input = scanner.next();
            checkQuit(input);
            System.out.println("Invalid input. Please enter a number.");
            System.out.print("Enter the index of the tile: ");
        }
        int tile1 = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter the direction of the edge you want to select (Up, Down, Left, Right): ");
        String direction = scanner.nextLine();
        checkQuit(direction);

        return new Object[]{tile1, direction};
    }

    public String getDirection() {
        return scanner.nextLine();
    }

    public int getReplayChoice() {
        System.out.println("\n--- Game Over Menu ---");
        System.out.println("1. Replay with same players");
        System.out.println("2. Return to main menu");
        System.out.println("3. Exit game");
        System.out.print("Enter your choice: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
            System.out.print("Enter your choice: ");
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return choice;
    }
}
