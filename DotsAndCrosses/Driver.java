public class Driver {
    public static void main(String[] args) {
        DotsCrossesManager manager = new DotsCrossesManager();
        manager.initGame();
        manager.gameLoop();
    }
}