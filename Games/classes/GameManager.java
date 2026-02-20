package classes;

import implementation.Player;

public abstract class GameManager{
    protected int moveCount;

    public GameManager() {
        moveCount = 0;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public abstract void initGame();
    public abstract void gameLoop();
    public abstract boolean gameEnd();
    public abstract Player getWinner();
}