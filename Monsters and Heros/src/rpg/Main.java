package rpg;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import rpg.encounter.MonsterRepository;
import rpg.world.Market;
import rpg.world.WorldConfig;

public final class Main {
    private Main() {
    }

    // Program entry point: loads game data, builds dependencies, and starts the game loop.
    public static void main(String[] args) {
        Path dataDirectory = args.length > 0
                ? Paths.get(args[0])
                : Paths.get("Legends_Monsters_and_Heroes");

        try {
            MonsterRepository repository = MonsterRepository.fromDataDirectory(dataDirectory);
            Market market = Market.fromDataDirectory(dataDirectory);
            GameController controller = new GameController(WorldConfig.defaultConfig(), repository, market);
            controller.run();
        } catch (IOException e) {
            System.err.println("Failed to load game data: " + e.getMessage());
        }
    }
}
