package io;

public class QuoridorInput extends Input {
    public QuoridorInput() {
        super();
    }

    @Override
    public String getUsername() {
        System.out.print("Enter your username: ");
        return scanner.nextLine();
    }

    public int getMoveChoice() {
        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Move pawn");
        System.out.println("2. Place wall");
        System.out.println("(Type 'quit' or 'q' to exit)");
        System.out.print("Enter your choice: ");
        
        while (!scanner.hasNextInt()) {
            String input = scanner.next();
            checkQuit(input);
            System.out.println("Invalid input. Please enter 1 or 2.");
            System.out.print("Enter your choice: ");
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return choice;
    }

    public int[] getPawnMove() {
        System.out.println("\nEnter the position to move to (or 'quit' to exit):");
        System.out.print("Row (0-8): ");
        while (!scanner.hasNextInt()) {
            String input = scanner.next();
            checkQuit(input);
            System.out.println("Invalid input. Please enter a number.");
            System.out.print("Row (0-8): ");
        }
        int row = scanner.nextInt();
        
        System.out.print("Column (0-8): ");
        while (!scanner.hasNextInt()) {
            String input = scanner.next();
            checkQuit(input);
            System.out.println("Invalid input. Please enter a number.");
            System.out.print("Column (0-8): ");
        }
        int col = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        return new int[]{row, col};
    }

    public Object[] getWallPlacement() {
        System.out.println("\nEnter wall placement (or 'quit' to exit):");
        System.out.print("Row (0-7): ");
        while (!scanner.hasNextInt()) {
            String input = scanner.next();
            checkQuit(input);
            System.out.println("Invalid input. Please enter a number.");
            System.out.print("Row (0-7): ");
        }
        int row = scanner.nextInt();
        
        System.out.print("Column (0-7): ");
        while (!scanner.hasNextInt()) {
            String input = scanner.next();
            checkQuit(input);
            System.out.println("Invalid input. Please enter a number.");
            System.out.print("Column (0-7): ");
        }
        int col = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        System.out.print("Orientation (H for horizontal, V for vertical): ");
        String orientation = scanner.nextLine().trim().toUpperCase();
        checkQuit(orientation);
        
        return new Object[]{row, col, orientation};
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
