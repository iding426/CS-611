package implementation.SlidingPuzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import classes.Board;
import classes.Tile;
import io.SlidingOutput;

public class SlidingBoard implements Board {
    private int rows;
    private int cols;
    private SlidingTile[][] grid;
    private int emptyRow;
    private int emptyCol;

    private SlidingTile[][] solved;
    private SlidingOutput output;

    public SlidingBoard(int rows, int cols) {
        grid = new SlidingTile[rows][cols];
        solved = new SlidingTile[rows][cols];

        this.rows = rows;
        this.cols = cols;
        this.output = new SlidingOutput();

        int counter = 1; // Value to put in the board
        
        // Start Solved
        emptyRow = rows - 1; 
        emptyCol = cols - 1;

        // Fill in the solved boards
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == emptyRow && j == emptyCol) {
                    grid[i][j] = new SlidingTile(i, j, 0);
                    solved[i][j] = new SlidingTile(i, j, 0);
                } else {
                    grid[i][j] = new SlidingTile(i, j, counter);
                    solved[i][j] = new SlidingTile(i, j, counter);
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

    // Slide a tile
    public void slide(int r, int c) {
        grid[emptyRow][emptyCol].setValue(grid[r][c].getValue());
        grid[r][c].setValue(0);
        emptyRow = r;
        emptyCol = c;
    }

    // Get the list of legal moves
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

    // Find the position of a tile by its value
    public int[] getTilePosition(int tileValue) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j].getValue() == tileValue) {
                    return new int[]{i, j};
                }
            }
        }
        return null; // Tile not found
    }

    // Check if a tile can be moved (is adjacent to empty space)
    public boolean canMoveTile(int tileValue) {
        int[] pos = getTilePosition(tileValue);
        if (pos == null) {
            return false;
        }

        int tileRow = pos[0];
        int tileCol = pos[1];

        // Check if tile is adjacent to empty space
        int rowDiff = Math.abs(tileRow - emptyRow);
        int colDiff = Math.abs(tileCol - emptyCol);

        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    // Slide a tile by its value
    public boolean slideTile(int tileValue) {
        if (!canMoveTile(tileValue)) {
            return false;
        }

        int[] pos = getTilePosition(tileValue);
        slide(pos[0], pos[1]);
        return true;
    } 

    // Check if a square is within the bounds of the board
    private boolean validSpace(int r, int c) {
        if (r < 0 || r >= this.rows || c < 0 || c >= this.cols) {
            return false;
        }

        return true;
    }

    // Check if the board is in the solved state
    public boolean isSolved() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (solved[i][j].getValue() != grid[i][j].getValue()) {
                    return false; 
                }
            }
        }

        return true;
    }

    // Board interface methods
    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return cols;
    }

    public Tile getTile(int row, int column) {
        if (row < 0 || row >= rows || column < 0 || column >= cols) {
            return null;
        }
        return grid[row][column];
    }

    public Tile[][] getBoard() {
        return grid;
    }

    public boolean neighbors(Tile a, Tile b) {
        if (a == null || b == null) {
            return false;
        }

        int rowDiff = Math.abs(a.getRow() - b.getRow());
        int colDiff = Math.abs(a.getColumn() - b.getColumn());

        // Neighbors if adjacent horizontally or vertically (not diagonally)
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    // Print out the board
    public void printBoard() {
        output.printBoard(grid, rows, cols);
    }
}

