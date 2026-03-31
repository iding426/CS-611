package rpg.item;

// Potion applies a temporary or immediate stat effect during battle.
public class Potion extends Item {
    private final int increaseAmount;
    private final PotionEffect effect;

    public Potion(String name, int cost, int requiredLevel, int increaseAmount, PotionEffect effect) {
        super(name, cost, requiredLevel);
        if (increaseAmount < 1 || effect == null) {
            throw new IllegalArgumentException("invalid potion values");
        }
        this.increaseAmount = increaseAmount;
        this.effect = effect;
    }

    public int getIncreaseAmount() {
        return increaseAmount;
    }

    public PotionEffect getEffect() {
        return effect;
    }
}
