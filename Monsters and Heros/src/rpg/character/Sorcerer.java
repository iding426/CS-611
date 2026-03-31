package rpg.character;

// Sorcerer favors dexterity and agility and gains mana faster.
public class Sorcerer extends Hero {
    public Sorcerer(String name, int level) {
        super(name, level);
    }

    @Override
    public String getHeroType() {
        return "Sorcerer";
    }

    @Override
    protected int strengthGainPerLevel() {
        return 6;
    }

    @Override
    protected int dexterityGainPerLevel() {
        return 12;
    }

    @Override
    protected int agilityGainPerLevel() {
        return 12;
    }

    @Override
    protected int manaGainPerLevel() {
        return 30;
    }
}
