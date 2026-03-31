package rpg.character;

public class Monster extends CharacterUnit {
    // Monsters are immutable combat templates once created.
    private final int damage;
    private final int defense;
    private final int dodgeChance;

    public Monster(String name, int level, int damage, int defense, int dodgeChance) {
        super(name, level);
        if (damage < 0 || defense < 0 || dodgeChance < 0 || dodgeChance > 100) {
            throw new IllegalArgumentException("invalid monster stats");
        }
        this.damage = damage;
        this.defense = defense;
        this.dodgeChance = dodgeChance;
    }

    public int getDamage() {
        return damage;
    }

    public int getDefense() {
        return defense;
    }

    public int getDodgeChance() {
        return dodgeChance;
    }
}
