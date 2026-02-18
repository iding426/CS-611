import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
    TODO:
        - Sliding Game Tile Class
        - Fill in Extended Methods
*/

public class SlidingBoard extends Board {
    private int rows;
    private int cols;
    private int[][] grid;
    private int emptyRow;
    private int emptyCol;

    private int[][] solved;

    public SlidingBoard(int rows, int cols) {
        grid = new int[rows][cols];
        solved = new int[rows][cols];

        this.rows = rows;
        this.cols = cols;

        int counter = 1; // Value to put in the board
        
        // Start Solved
        emptyRow = rows - 1; 
        emptyCol = cols - 1;

        // Fill in the solved boards
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == emptyRow && j == emptyCol) {
                    grid[i][j] = 0;
                    solved[i][j] = 0;
                } else {
                    grid[i][j] = counter;
                    solved[i][j] = counter;
                    counter++;
                }
            }
        }
    }

    // Random start state that is solvable
    public void shuffle() {
        Random rand = new Random();

        int scramble = rows * cols * (rand.nextInt(5)+ 5); // Random from 5-10x the area

        for (int i = 0; i < scramble; i++) {
            List<int[]> options = availableMoves();
            int[] move = options.get(rand.nextInt(options.size()));

            slide(move[0], move[1]);
        }
    }

    public void slide(int r, int c) {
        grid[emptyRow][emptyCol] = grid[r][c];
        grid[r][c] = 0;
        emptyRow = r;
        emptyCol = c;
    }

    public List<int[]> availableMoves() {
        int[][] directions = {{-1,0},{0,-1},{1,0},{0,1}};

        List<int[]> positions = new ArrayList<>();


        for (int[] dir : directions) {
            int dr = dir[0];
            int dc = dir[1];

            int newRow = emptyRow + dr;
            int newCol = emptyCol + dc;

            if (validSpace(newRow, newCol)) {
                positions.add(new int[]{newRow,newCol});
            }
        }

        return positions;
    } 

    private boolean validSpace(int r, int c) {
        if (r < 0 || r >= this.rows || c < 0 || c >= this.cols) {
            return false;
        }

        return true;
    }

    public boolean isSolved() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (solved[i][j] != grid[i][j]) {
                    return false; 
                }
            }
        }

        return true;
    }

    // Print out the board
    public void printBoard() {
        // Print top border
        for (int j = 0; j < cols; j++) {
            System.out.print("+---");
        }
        System.out.println("+");
        
        // Print each row
        for (int i = 0; i < rows; i++) {
            // Print cell values
            for (int j = 0; j < cols; j++) {
                System.out.print("| ");
                if (grid[i][j] != 0) {
                    System.out.print(grid[i][j]);
                } else {
                    System.out.print("_");
                }
                System.out.print(" ");
            }
            System.out.println("|");
            
            // Print horizontal border after each row
            for (int j = 0; j < cols; j++) {
                System.out.print("+---");
            }
            System.out.println("+");
        }
    }
}

