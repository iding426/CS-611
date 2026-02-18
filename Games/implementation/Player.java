/*
    TODO:
        - Separate Player class for each game
        - Track statistics
*/

class Player {
    String username;
    int moveCount;

    public Player(String username) {
        this.username = username;
        moveCount = 0;
    }

    public String getUsername() {
        return username;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void incrementMoveCount() {
        moveCount++;
    }

    public void resetMoveCount() {
        moveCount = 0;
    }
}