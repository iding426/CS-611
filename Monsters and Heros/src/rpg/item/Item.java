package rpg.item;

// Base metadata shared by all market/inventory items.
public abstract class Item {
    private final String name;
    private final int cost;
    private final int requiredLevel;

    protected Item(String name, int cost, int requiredLevel) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        if (cost < 0 || requiredLevel < 1) {
            throw new IllegalArgumentException("invalid item values");
        }
        this.name = name;
        this.cost = cost;
        this.requiredLevel = requiredLevel;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }
}
