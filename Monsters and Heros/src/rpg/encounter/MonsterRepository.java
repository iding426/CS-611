package rpg.encounter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import rpg.character.Monster;

public class MonsterRepository {
    private static final String[] MONSTER_FILES = {
            "Dragons.txt",
            "Exoskeletons.txt",
            "Spirits.txt"
    };

    private final List<MonsterTemplate> templates;

    private MonsterRepository(List<MonsterTemplate> templates) {
        this.templates = List.copyOf(templates);
    }

    // Loads all monster families from the configured text files.
    public static MonsterRepository fromDataDirectory(Path dataDirectory) throws IOException {
        List<MonsterTemplate> parsedTemplates = new ArrayList<>();
        for (String fileName : MONSTER_FILES) {
            Path filePath = dataDirectory.resolve(fileName);
            parsedTemplates.addAll(parseFile(filePath));
        }
        if (parsedTemplates.isEmpty()) {
            throw new IOException("No monster records found in " + dataDirectory);
        }
        return new MonsterRepository(parsedTemplates);
    }

    public List<MonsterTemplate> getAllTemplates() {
        return templates;
    }

    public List<MonsterTemplate> getTemplatesUpToLevel(int level) {
        List<MonsterTemplate> eligible = templates.stream()
                .filter(template -> template.level() <= level)
                .toList();
        // Fallback to all templates so encounter generation never becomes empty.
        return eligible.isEmpty() ? templates : eligible;
    }

    // Parses a single monster file (skipping header row).
    private static List<MonsterTemplate> parseFile(Path filePath) throws IOException {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines
                    .skip(1)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(MonsterRepository::parseLine)
                    .toList();
        }
    }

    // Converts one tab/space-delimited row into a typed monster template.
    private static MonsterTemplate parseLine(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid monster row: " + line);
        }

        String name = parts[0];
        int level = Integer.parseInt(parts[1]);
        int damage = Integer.parseInt(parts[2]);
        int defense = Integer.parseInt(parts[3]);
        int dodgeChance = Integer.parseInt(parts[4]);
        return new MonsterTemplate(name, level, damage, defense, dodgeChance);
    }

    public record MonsterTemplate(String name, int level, int damage, int defense, int dodgeChance) {
        public Monster toMonster() {
            return new Monster(name, level, damage, defense, dodgeChance);
        }
    }
}
