package io;

// Exception thrown when user wants to quit the game
public class QuitGameException extends RuntimeException {
    public QuitGameException() {
        super("User requested to quit the game");
    }
}
