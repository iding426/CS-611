package io;

public class SlidingInput extends Input {
    public SlidingInput() {
        super();
    }

    @Override
    public String getUsername() {
        System.out.print("Enter your username: ");
        return scanner.nextLine();
    }

    public int getDimension(String dimensionName) {
        int value = 0;
        while (value <= 0) {
            System.out.print("Enter number of " + dimensionName + " (or type 'quit' to exit): ");
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                if (value <= 1) {
                    System.out.println("Value must be at least 2.");
                }
            } else {
                String input = scanner.next();
                checkQuit(input);
                System.out.println("Please enter a valid integer.");
            }
        }
        return value;
    }

    public int getTileToSlide() {
        System.out.print("Enter the number of the tile you want to move (or -1 to quit, or type 'quit'): ");
        while (!scanner.hasNextInt()) {
            String input = scanner.next();
            checkQuit(input);
            System.out.println("Invalid input. Please enter a number.");
            System.out.print("Enter the number of the tile you want to move (or -1 to quit, or type 'quit'): ");
        }
        return scanner.nextInt();
    }

    public int getReplayChoice() {
        System.out.println("\n--- Game Over Menu ---");
        System.out.println("1. Play again");
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
