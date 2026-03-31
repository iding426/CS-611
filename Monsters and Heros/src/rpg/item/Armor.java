package rpg.item;

// Armor reduces incoming physical damage while equipped.
public class Armor extends Item {
    private final int damageReduction;

    public Armor(String name, int cost, int requiredLevel, int damageReduction) {
        super(name, cost, requiredLevel);
        if (damageReduction < 0) {
            throw new IllegalArgumentException("damageReduction must be >= 0");
        }
        this.damageReduction = damageReduction;
    }

    public int getDamageReduction() {
        return damageReduction;
    }
}
