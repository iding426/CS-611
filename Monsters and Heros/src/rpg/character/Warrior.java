package rpg.character;

// Warrior favors strength and agility growth for weapon-heavy combat.
public class Warrior extends Hero {
    public Warrior(String name, int level) {
        super(name, level);
    }

    @Override
    public String getHeroType() {
        return "Warrior";
    }

    @Override
    protected int strengthGainPerLevel() {
        return 12;
    }

    @Override
    protected int dexterityGainPerLevel() {
        return 6;
    }

    @Override
    protected int agilityGainPerLevel() {
        return 12;
    }
}
