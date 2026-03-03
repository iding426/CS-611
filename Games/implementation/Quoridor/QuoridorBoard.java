package implementation.Quoridor;

import java.util.*;
import classes.Board;
import classes.Tile;

public class QuoridorBoard implements Board {
    private static final int SIZE = 9;
    private QuoridorTile[][] board;
    private Set<String> horizontalWalls; // Walls blocking vertical movement
    private Set<String> verticalWalls;   // Walls blocking horizontal movement

    public QuoridorBoard() {
        board = new QuoridorTile[SIZE][SIZE];
        horizontalWalls = new HashSet<>();
        verticalWalls = new HashSet<>();
        
        // Initialize tiles
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = new QuoridorTile(row, col);
            }
        }
        
        // Set initial player positions
        board[0][4].setPlayer1(true);
        board[8][4].setPlayer2(true);
    }

    @Override
    public int getRows() {
        return SIZE;
    }

    @Override
    public int getColumns() {
        return SIZE;
    }

    @Override
    public Tile getTile(int row, int column) {
        if (row >= 0 && row < SIZE && column >= 0 && column < SIZE) {
            return board[row][column];
        }
        return null;
    }

    @Override
    public Tile[][] getBoard() {
        return board;
    }

    @Override
    public boolean neighbors(Tile a, Tile b) {
        if (a == null || b == null) return false;
        
        int rowDiff = Math.abs(a.getRow() - b.getRow());
        int colDiff = Math.abs(a.getColumn() - b.getColumn());
        
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    // Check if a pawn can move from one position to another
    public boolean isValidPawnMove(int fromRow, int fromCol, int toRow, int toCol, 
                                    int opponentRow, int opponentCol) {
        // Check if destination is on the board
        if (toRow < 0 || toRow >= SIZE || toCol < 0 || toCol >= SIZE) {
            return false;
        }

        // Calculate movement direction
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        // Check for simple adjacent move
        if (Math.abs(rowDiff) + Math.abs(colDiff) == 1) {
            // Check if there's no wall blocking this move
            return !isWallBlocking(fromRow, fromCol, toRow, toCol);
        }

        // Check for jump move (over opponent)
        if (Math.abs(rowDiff) + Math.abs(colDiff) == 2) {
            // Must be jumping over opponent
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            
            if (midRow != opponentRow || midCol != opponentCol) {
                return false;
            }

            // Check if we can move to opponent's position
            if (isWallBlocking(fromRow, fromCol, midRow, midCol)) {
                return false;
            }

            // Check if we can jump over opponent
            if (isWallBlocking(midRow, midCol, toRow, toCol)) {
                return false;
            }

            return true;
        }

        // Check for diagonal jump (when blocked behind opponent)
        if (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1) {
            // Check if opponent is adjacent
            if ((Math.abs(fromRow - opponentRow) + Math.abs(fromCol - opponentCol)) != 1) {
                return false;
            }

            // Check if we can move to opponent's position
            if (isWallBlocking(fromRow, fromCol, opponentRow, opponentCol)) {
                return false;
            }

            // Check if the direct jump is blocked
            int directJumpRow = opponentRow + (opponentRow - fromRow);
            int directJumpCol = opponentCol + (opponentCol - fromCol);
            
            if (directJumpRow < 0 || directJumpRow >= SIZE || 
                directJumpCol < 0 || directJumpCol >= SIZE ||
                !isWallBlocking(opponentRow, opponentCol, directJumpRow, directJumpCol)) {
                return false; // Not a valid diagonal jump scenario
            }

            // Check if diagonal move is not blocked
            if (isWallBlocking(opponentRow, opponentCol, toRow, toCol)) {
                return false;
            }

            return true;
        }

        return false;
    }

    // Check if a wall blocks movement between two adjacent tiles
    private boolean isWallBlocking(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow == toRow) {
            // Horizontal movement - check vertical walls
            int row = fromRow;
            int minCol = Math.min(fromCol, toCol);
            
            // Check if there's a vertical wall blocking this passage
            String wall1 = wallKey(row, minCol);
            if (verticalWalls.contains(wall1)) return true;
            
            // Check if there's a vertical wall above or below that blocks this passage
            if (row > 0) {
                String wall2 = wallKey(row - 1, minCol);
                if (verticalWalls.contains(wall2)) return true;
            }
        } else {
            // Vertical movement - check horizontal walls
            int col = fromCol;
            int minRow = Math.min(fromRow, toRow);
            
            // Check if there's a horizontal wall blocking this passage
            String wall1 = wallKey(minRow, col);
            if (horizontalWalls.contains(wall1)) return true;
            
            // Check if there's a horizontal wall to the left or right that blocks this passage
            if (col > 0) {
                String wall2 = wallKey(minRow, col - 1);
                if (horizontalWalls.contains(wall2)) return true;
            }
        }
        
        return false;
    }

    // Place a wall at the given position
    public boolean placeWall(int row, int col, String orientation, 
                            QuoridorPlayer p1, QuoridorPlayer p2) {
        // Validate position
        if (row < 0 || row >= SIZE - 1 || col < 0 || col >= SIZE - 1) {
            return false;
        }

        String wallKey = wallKey(row, col);
        
        if (orientation.equals("H")) {
            // Check if this wall or an overlapping wall already exists
            if (horizontalWalls.contains(wallKey)) {
                return false;
            }
            
            // Check for intersecting walls
            if (col > 0 && horizontalWalls.contains(wallKey(row, col - 1))) {
                return false;
            }
            if (col < SIZE - 2 && horizontalWalls.contains(wallKey(row, col + 1))) {
                return false;
            }
            if (verticalWalls.contains(wallKey)) {
                return false;
            }
            
            // Temporarily place wall
            horizontalWalls.add(wallKey);
            
            // Check if both players can still reach their goals
            if (!canReachGoal(p1) || !canReachGoal(p2)) {
                horizontalWalls.remove(wallKey);
                return false;
            }
            
            return true;
            
        } else if (orientation.equals("V")) {
            // Check if this wall or an overlapping wall already exists
            if (verticalWalls.contains(wallKey)) {
                return false;
            }
            
            // Check for intersecting walls
            if (row > 0 && verticalWalls.contains(wallKey(row - 1, col))) {
                return false;
            }
            if (row < SIZE - 2 && verticalWalls.contains(wallKey(row + 1, col))) {
                return false;
            }
            if (horizontalWalls.contains(wallKey)) {
                return false;
            }
            
            // Temporarily place wall
            verticalWalls.add(wallKey);
            
            // Check if both players can still reach their goals
            if (!canReachGoal(p1) || !canReachGoal(p2)) {
                verticalWalls.remove(wallKey);
                return false;
            }
            
            return true;
        }
        
        return false;
    }

    private String wallKey(int row, int col) {
        return row + "," + col;
    }

    // Check if a player can reach their goal using BFS
    private boolean canReachGoal(QuoridorPlayer player) {
        int startRow = player.getCurrentRow();
        int startCol = player.getCurrentCol();
        int goalRow = player.getGoalRow();
        
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[SIZE][SIZE];
        
        queue.offer(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;
        
        // Get opponent position (not used in pathfinding check, just checking reachability)
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];
            
            // Check if we reached the goal row
            if (row == goalRow) {
                return true;
            }
            
            // Try all four directions
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                
                if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE && 
                    !visited[newRow][newCol] && !isWallBlocking(row, col, newRow, newCol)) {
                    visited[newRow][newCol] = true;
                    queue.offer(new int[]{newRow, newCol});
                }
            }
        }
        
        return false;
    }

    // Move a player on the board
    public void movePawn(QuoridorPlayer player, int toRow, int toCol) {
        int fromRow = player.getCurrentRow();
        int fromCol = player.getCurrentCol();
        
        // Clear old position
        if (player.isPlayerOne()) {
            board[fromRow][fromCol].setPlayer1(false);
            board[toRow][toCol].setPlayer1(true);
        } else {
            board[fromRow][fromCol].setPlayer2(false);
            board[toRow][toCol].setPlayer2(true);
        }
        
        player.setPosition(toRow, toCol);
    }

    // Generate board string representation
    public String getBoardString(QuoridorPlayer p1, QuoridorPlayer p2) {
        StringBuilder sb = new StringBuilder();
        
        // Column numbers
        sb.append("   ");
        for (int col = 0; col < SIZE; col++) {
            sb.append(" ").append(col).append("  ");
        }
        sb.append("\n");
        
        for (int row = 0; row < SIZE; row++) {
            // Row number and tiles
            sb.append(row).append("  ");
            for (int col = 0; col < SIZE; col++) {
                QuoridorTile tile = board[row][col];
                if (tile.hasPlayer1()) {
                    sb.append(" 1 ");
                } else if (tile.hasPlayer2()) {
                    sb.append(" 2 ");
                } else {
                    sb.append(" · ");
                }
                
                // Vertical wall to the right
                if (col < SIZE - 1) {
                    if (verticalWalls.contains(wallKey(row, col)) || 
                        (row > 0 && verticalWalls.contains(wallKey(row - 1, col)))) {
                        sb.append("║");
                    } else {
                        sb.append(" ");
                    }
                }
            }
            sb.append("\n");
            
            // Horizontal walls below
            if (row < SIZE - 1) {
                sb.append("   ");
                for (int col = 0; col < SIZE; col++) {
                    if (horizontalWalls.contains(wallKey(row, col)) || 
                        (col > 0 && horizontalWalls.contains(wallKey(row, col - 1)))) {
                        sb.append("═══");
                    } else {
                        sb.append("   ");
                    }
                    
                    if (col < SIZE - 1) {
                        sb.append(" ");
                    }
                }
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }

    public Set<String> getHorizontalWalls() {
        return horizontalWalls;
    }

    public Set<String> getVerticalWalls() {
        return verticalWalls;
    }
}
