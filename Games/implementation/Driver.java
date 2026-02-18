/*
    TODO:
        - Implement Game choosing capabilities
        - Player statistics for multiple games
        - Ways to switch games
*/

public class Driver {
    public static void main(String[] args) {
        // DotsCrossesManager manager = new DotsCrossesManager();
        // manager.initGame();
        // manager.gameLoop();

        SlidingPuzzleManager manager = new SlidingPuzzleManager();
        manager.start();
    }
}
