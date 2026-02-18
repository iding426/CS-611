abstract class GameManager{
    int moveCount;

    public GameManager() {
        moveCount = 0;
    }

    public int getMoveCount() {
        return moveCount;
    }

    abstract void initGame();
    abstract void gameLoop();
    abstract boolean gameEnd();
    abstract Player getWinner();
}