package implementation;

import implementation.DotsAndCrosses.DotsCrossesManager;
import implementation.SlidingPuzzle.SlidingPuzzleManager;
import io.DriverInput;
import io.DriverOutput;

public class Driver {
    private static DriverInput input;
    private static DriverOutput output;

    public static void main(String[] args) {
        input = new DriverInput();
        output = new DriverOutput();

        output.printWelcome();

        boolean keepPlaying = true;
        while (keepPlaying) {
            output.printGameMenu();
            int choice = input.getGameChoice();

            switch (choice) {
                case 1:
                    playDotsAndCrosses();
                    break;
                case 2:
                    playSlidingPuzzle();
                    break;
                case 3:
                    keepPlaying = false;
                    break;
                default:
                    output.printInvalidChoice();
            }

            if (keepPlaying && choice >= 1 && choice <= 2) {
                output.printSeparator();
                keepPlaying = input.playAnotherGame();
            }
        }

        output.printGoodbye();
    }

    private static void playDotsAndCrosses() {
        DotsCrossesManager manager = new DotsCrossesManager();
        manager.playWithReplay();
    }

    private static void playSlidingPuzzle() {
        SlidingPuzzleManager manager = new SlidingPuzzleManager();
        manager.start();
    }
}
