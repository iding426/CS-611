package rpg.character;

// Paladin favors strength and dexterity for balanced offense/defense growth.
public class Paladin extends Hero {
    public Paladin(String name, int level) {
        super(name, level);
    }

    @Override
    public String getHeroType() {
        return "Paladin";
    }

    @Override
    protected int strengthGainPerLevel() {
        return 12;
    }

    @Override
    protected int dexterityGainPerLevel() {
        return 12;
    }

    @Override
    protected int agilityGainPerLevel() {
        return 6;
    }
}
