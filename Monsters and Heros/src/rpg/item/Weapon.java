package rpg.item;

// Weapon contributes direct physical damage and may require one or two hands.
public class Weapon extends Item {
    private final int damage;
    private final int requiredHands;

    public Weapon(String name, int cost, int requiredLevel, int damage, int requiredHands) {
        super(name, cost, requiredLevel);
        if (damage < 1 || (requiredHands != 1 && requiredHands != 2)) {
            throw new IllegalArgumentException("invalid weapon values");
        }
        this.damage = damage;
        this.requiredHands = requiredHands;
    }

    public int getDamage() {
        return damage;
    }

    public int getRequiredHands() {
        return requiredHands;
    }
}
