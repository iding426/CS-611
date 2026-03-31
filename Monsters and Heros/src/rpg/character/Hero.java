package rpg.character;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rpg.item.Armor;
import rpg.item.Potion;
import rpg.item.Spell;
import rpg.item.Weapon;

public abstract class Hero extends CharacterUnit {
    private int experience;
    private int money;
    private int strength;
    private int dexterity;
    private int agility;
    private int maxMana;
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private final List<Weapon> weapons;
    private final List<Armor> armors;
    private final List<Spell> spells;
    private final List<Potion> potions;

    protected Hero(String name, int level) {
        super(name, level);
        this.experience = 0;
        this.money = 0;
        this.strength = 100 + level * 20;
        this.dexterity = 100 + level * 15;
        this.agility = 100 + level * 10;
        this.maxMana = 100 + level * 40;
        this.weapons = new ArrayList<>();
        this.armors = new ArrayList<>();
        this.spells = new ArrayList<>();
        this.potions = new ArrayList<>();
    }

    public int getExperience() {
        return experience;
    }

    public int getMoney() {
        return money;
    }

    public int getStrength() {
        return strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getAgility() {
        return agility;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    public List<Weapon> getWeapons() {
        return Collections.unmodifiableList(weapons);
    }

    public List<Armor> getArmors() {
        return Collections.unmodifiableList(armors);
    }

    public List<Spell> getSpells() {
        return Collections.unmodifiableList(spells);
    }

    public List<Potion> getPotions() {
        return Collections.unmodifiableList(potions);
    }

    public abstract String getHeroType();

    protected abstract int strengthGainPerLevel();

    protected abstract int dexterityGainPerLevel();

    protected abstract int agilityGainPerLevel();

    protected int manaGainPerLevel() {
        return 25;
    }

    // Base HP formula shared by all hero classes.
    public int getMaxHp() {
        return 600 + getLevel() * 120;
    }

    // Computes physical attack strength including temporary battle buffs.
    public int calculateAttackPower(int temporaryBonus) {
        return strength / 2 + getLevel() * 20 + temporaryBonus;
    }

    // Defense combines dexterity, equipped armor, and temporary bonuses.
    public int calculateDefense(int temporaryBonus) {
        int armorReduction = equippedArmor == null ? 0 : equippedArmor.getDamageReduction();
        return dexterity / 4 + armorReduction / 6 + getLevel() * 8 + temporaryBonus;
    }

    // Dodge chance is capped to avoid near-guaranteed evasion.
    public int calculateDodgeChance(int temporaryBonus) {
        return Math.min(60, agility / 12 + getLevel() * 2 + temporaryBonus);
    }

    public int calculateWeaponDamageAgainst(int monsterDefense, int temporaryBonus) {
        int weaponDamage = equippedWeapon != null ? equippedWeapon.getDamage() : 80;
        return Math.max(30, calculateAttackPower(temporaryBonus) + weaponDamage / 6 - monsterDefense / 3);
    }

    public int calculateSpellDamageAgainst(Spell spell, int monsterDefense, int temporaryBonus) {
        return Math.max(
                50,
                spell.getDamage() + temporaryBonus + getDexterity() / 8 - monsterDefense / 4);
    }

    public void addExperience(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be >= 0");
        }
        experience += amount;
        int previousLevel = getLevel();
        int expectedLevel = Math.max(1, experience / 100 + 1);
        if (expectedLevel > getLevel()) {
            // Stat growth is delegated to subclass-specific growth rates.
            setLevel(expectedLevel);
            int gainedLevels = expectedLevel - previousLevel;
            strength += gainedLevels * strengthGainPerLevel();
            dexterity += gainedLevels * dexterityGainPerLevel();
            agility += gainedLevels * agilityGainPerLevel();
            maxMana += gainedLevels * manaGainPerLevel();
        }
    }

    public void addMoney(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be >= 0");
        }
        money += amount;
    }

    public boolean canAfford(int amount) {
        return amount >= 0 && money >= amount;
    }

    public boolean spendMoney(int amount) {
        if (amount < 0) {
            return false;
        }
        if (money < amount) {
            return false;
        }
        money -= amount;
        return true;
    }

    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);
        if (equippedWeapon == null) {
            equippedWeapon = weapon;
        }
    }

    public void addArmor(Armor armor) {
        armors.add(armor);
        if (equippedArmor == null) {
            equippedArmor = armor;
        }
    }

    public void addSpell(Spell spell) {
        spells.add(spell);
    }

    public void addPotion(Potion potion) {
        potions.add(potion);
    }

    public boolean equipWeapon(int index) {
        if (index < 0 || index >= weapons.size()) {
            return false;
        }
        Weapon candidate = weapons.get(index);
        if (getLevel() < candidate.getRequiredLevel()) {
            return false;
        }
        equippedWeapon = candidate;
        return true;
    }

    public boolean equipArmor(int index) {
        if (index < 0 || index >= armors.size()) {
            return false;
        }
        Armor candidate = armors.get(index);
        if (getLevel() < candidate.getRequiredLevel()) {
            return false;
        }
        equippedArmor = candidate;
        return true;
    }

    public Potion removePotion(int index) {
        if (index < 0 || index >= potions.size()) {
            return null;
        }
        return potions.remove(index);
    }

    public Weapon removeWeapon(int index) {
        if (index < 0 || index >= weapons.size()) {
            return null;
        }
        Weapon removed = weapons.remove(index);
        if (removed == equippedWeapon) {
            equippedWeapon = weapons.isEmpty() ? null : weapons.get(0);
        }
        return removed;
    }

    public Armor removeArmor(int index) {
        if (index < 0 || index >= armors.size()) {
            return null;
        }
        Armor removed = armors.remove(index);
        if (removed == equippedArmor) {
            equippedArmor = armors.isEmpty() ? null : armors.get(0);
        }
        return removed;
    }

    public Spell removeSpell(int index) {
        if (index < 0 || index >= spells.size()) {
            return null;
        }
        return spells.remove(index);
    }
}
