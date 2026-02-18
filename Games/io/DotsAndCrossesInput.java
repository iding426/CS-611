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
        System.out.println("Now enter the edge you want to select!");
        System.out.print("Enter the index of the tile: ");
        int tile1 = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter the direction of the edge you want to select (Up, Down, Left, Right): ");
        String direction = scanner.nextLine();

        return new Object[]{tile1, direction};
    }

    public String getDirection() {
        return scanner.nextLine();
    }
}
