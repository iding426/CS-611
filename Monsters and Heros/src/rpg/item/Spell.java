package rpg.item;

// Spell has burst damage plus a mana cost and school-specific side effect in battle.
public class Spell extends Item {
    private final int damage;
    private final int manaCost;
    private final SpellSchool school;

    public Spell(String name, int cost, int requiredLevel, int damage, int manaCost, SpellSchool school) {
        super(name, cost, requiredLevel);
        if (damage < 1 || manaCost < 0 || school == null) {
            throw new IllegalArgumentException("invalid spell values");
        }
        this.damage = damage;
        this.manaCost = manaCost;
        this.school = school;
    }

    public int getDamage() {
        return damage;
    }

    public int getManaCost() {
        return manaCost;
    }

    public SpellSchool getSchool() {
        return school;
    }
}
