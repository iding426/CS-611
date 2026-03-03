package io;

import java.util.Scanner;

public abstract class Input {
    protected Scanner scanner;

    public Input() {
        scanner = new Scanner(System.in);
    }

    public abstract String getUsername();
    
    // Check if user wants to quit
    protected void checkQuit(String input) {
        if (input != null && (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("q"))) {
            throw new QuitGameException();
        }
    }
}
