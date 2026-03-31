package rpg.character;

public abstract class CharacterUnit {
    private final String name;
    private int level;

    protected CharacterUnit(String name, int level) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        if (level < 1) {
            throw new IllegalArgumentException("level must be >= 1");
        }
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    // Level changes are protected so only subclasses/control logic can update progression.
    protected void setLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("level must be >= 1");
        }
        this.level = level;
    }
}
