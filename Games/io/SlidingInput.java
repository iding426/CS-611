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

    public int getRowToSlide() {
        System.out.println("Enter the row and column of the tile to slide (or -1 to quit):");
        return scanner.nextInt();
    }

    public int getColumnToSlide() {
        return scanner.nextInt();
    }
}
