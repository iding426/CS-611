package rpg.world;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Stream;

import rpg.character.Hero;
import rpg.item.Armor;
import rpg.item.Item;
import rpg.item.Potion;
import rpg.item.PotionEffect;
import rpg.item.Spell;
import rpg.item.SpellSchool;
import rpg.item.Weapon;

public class Market {
    private final List<Weapon> weapons;
    private final List<Armor> armors;
    private final List<Spell> spells;
    private final List<Potion> potions;

    private Market(List<Weapon> weapons, List<Armor> armors, List<Spell> spells, List<Potion> potions) {
        this.weapons = List.copyOf(weapons);
        this.armors = List.copyOf(armors);
        this.spells = List.copyOf(spells);
        this.potions = List.copyOf(potions);
    }

    // Loads all market catalog items from the provided data directory.
    public static Market fromDataDirectory(Path dataDirectory) throws IOException {
        List<Weapon> weapons = parseWeapons(dataDirectory.resolve("Weaponry.txt"));
        List<Armor> armors = parseArmors(dataDirectory.resolve("Armory.txt"));
        List<Potion> potions = parsePotions(dataDirectory.resolve("Potions.txt"));
        List<Spell> spells = new ArrayList<>();
        spells.addAll(parseSpells(dataDirectory.resolve("FireSpells.txt"), SpellSchool.FIRE));
        spells.addAll(parseSpells(dataDirectory.resolve("IceSpells.txt"), SpellSchool.ICE));
        spells.addAll(parseSpells(dataDirectory.resolve("LightningSpells.txt"), SpellSchool.LIGHTNING));
        return new Market(weapons, armors, spells, potions);
    }

    // Top-level market interaction loop: choose hero, then buy/sell until the user exits.
    public void openMarket(Scanner scanner, List<Hero> party) {
        System.out.println("Welcome to the market.");
        boolean inMarket = true;
        while (inMarket) {
            // Trade operations are always done for one chosen hero at a time.
            Hero selectedHero = chooseHero(scanner, party);
            if (selectedHero == null) {
                inMarket = false;
                continue;
            }

            boolean withHero = true;
            while (withHero) {
                System.out.printf("%n%s (%s) - Gold: %d%n",
                        selectedHero.getName(),
                        selectedHero.getHeroType(),
                        selectedHero.getMoney());
                System.out.println("1. Buy");
                System.out.println("2. Sell");
                System.out.println("3. Choose another hero");
                System.out.println("0. Leave market");
                int action = promptIntInRange(scanner, "Choice: ", 0, 3);
                switch (action) {
                    case 1 -> runBuyFlow(scanner, selectedHero);
                    case 2 -> runSellFlow(scanner, selectedHero);
                    case 3 -> withHero = false;
                    case 0 -> {
                        withHero = false;
                        inMarket = false;
                    }
                    default -> {
                    }
                }
            }
        }
        System.out.println("You leave the market.");
    }

    // Buy menu router by item category.
    private void runBuyFlow(Scanner scanner, Hero hero) {
        System.out.println("Buy category: 1. Weapon  2. Armor  3. Spell  4. Potion  0. Back");
        int category = promptIntInRange(scanner, "Category: ", 0, 4);
        switch (category) {
            case 1 -> buyItem(scanner, hero, weapons, hero::addWeapon, this::weaponLabel);
            case 2 -> buyItem(scanner, hero, armors, hero::addArmor, this::armorLabel);
            case 3 -> buyItem(scanner, hero, spells, hero::addSpell, this::spellLabel);
            case 4 -> buyItem(scanner, hero, potions, hero::addPotion, this::potionLabel);
            case 0 -> {
            }
            default -> {
            }
        }
    }

    // Sell menu router by item category.
    private void runSellFlow(Scanner scanner, Hero hero) {
        System.out.println("Sell category: 1. Weapon  2. Armor  3. Spell  4. Potion  0. Back");
        int category = promptIntInRange(scanner, "Category: ", 0, 4);
        switch (category) {
            case 1 -> sellHeroWeapon(scanner, hero);
            case 2 -> sellHeroArmor(scanner, hero);
            case 3 -> sellHeroSpell(scanner, hero);
            case 4 -> sellHeroPotion(scanner, hero);
            case 0 -> {
            }
            default -> {
            }
        }
    }

    // Generic item purchase flow reused for all buyable categories.
    private <T extends Item> void buyItem(
            Scanner scanner,
            Hero hero,
            List<T> stock,
            Consumer<T> addToHero,
            java.util.function.Function<T, String> labelFn) {
        if (stock.isEmpty()) {
            System.out.println("No items in this category.");
            return;
        }

        for (int i = 0; i < stock.size(); i++) {
            T item = stock.get(i);
            String status = hero.getLevel() >= item.getRequiredLevel() ? "" : " (level too low)";
            System.out.printf("%d. %s%s%n", i + 1, labelFn.apply(item), status);
        }
        System.out.println("0. Cancel");
        int pick = promptIntInRange(scanner, "Buy which item: ", 0, stock.size());
        if (pick == 0) {
            return;
        }

        T item = stock.get(pick - 1);
        if (hero.getLevel() < item.getRequiredLevel()) {
            System.out.println("Hero level is too low for this item.");
            return;
        }
        // The purchase only succeeds when both level and gold requirements are met.
        if (!hero.spendMoney(item.getCost())) {
            System.out.println("Not enough gold.");
            return;
        }
        addToHero.accept(item);
        System.out.printf("Bought %s for %d gold.%n", item.getName(), item.getCost());
    }

    private void sellHeroWeapon(Scanner scanner, Hero hero) {
        if (hero.getWeapons().isEmpty()) {
            System.out.println("No weapons to sell.");
            return;
        }
        for (int i = 0; i < hero.getWeapons().size(); i++) {
            Weapon weapon = hero.getWeapons().get(i);
            int price = resaleValue(weapon);
            String equipped = weapon == hero.getEquippedWeapon() ? " [equipped]" : "";
            System.out.printf("%d. %s (sell %d)%s%n", i + 1, weapon.getName(), price, equipped);
        }
        System.out.println("0. Cancel");
        int pick = promptIntInRange(scanner, "Sell which weapon: ", 0, hero.getWeapons().size());
        if (pick == 0) {
            return;
        }
        Weapon sold = hero.removeWeapon(pick - 1);
        int price = resaleValue(sold);
        hero.addMoney(price);
        System.out.printf("Sold %s for %d gold.%n", sold.getName(), price);
    }

    private void sellHeroArmor(Scanner scanner, Hero hero) {
        if (hero.getArmors().isEmpty()) {
            System.out.println("No armors to sell.");
            return;
        }
        for (int i = 0; i < hero.getArmors().size(); i++) {
            Armor armor = hero.getArmors().get(i);
            int price = resaleValue(armor);
            String equipped = armor == hero.getEquippedArmor() ? " [equipped]" : "";
            System.out.printf("%d. %s (sell %d)%s%n", i + 1, armor.getName(), price, equipped);
        }
        System.out.println("0. Cancel");
        int pick = promptIntInRange(scanner, "Sell which armor: ", 0, hero.getArmors().size());
        if (pick == 0) {
            return;
        }
        Armor sold = hero.removeArmor(pick - 1);
        int price = resaleValue(sold);
        hero.addMoney(price);
        System.out.printf("Sold %s for %d gold.%n", sold.getName(), price);
    }

    private void sellHeroSpell(Scanner scanner, Hero hero) {
        if (hero.getSpells().isEmpty()) {
            System.out.println("No spells to sell.");
            return;
        }
        for (int i = 0; i < hero.getSpells().size(); i++) {
            Spell spell = hero.getSpells().get(i);
            int price = resaleValue(spell);
            System.out.printf("%d. %s (sell %d)%n", i + 1, spell.getName(), price);
        }
        System.out.println("0. Cancel");
        int pick = promptIntInRange(scanner, "Sell which spell: ", 0, hero.getSpells().size());
        if (pick == 0) {
            return;
        }
        Spell sold = hero.removeSpell(pick - 1);
        int price = resaleValue(sold);
        hero.addMoney(price);
        System.out.printf("Sold %s for %d gold.%n", sold.getName(), price);
    }

    private void sellHeroPotion(Scanner scanner, Hero hero) {
        if (hero.getPotions().isEmpty()) {
            System.out.println("No potions to sell.");
            return;
        }
        for (int i = 0; i < hero.getPotions().size(); i++) {
            Potion potion = hero.getPotions().get(i);
            int price = resaleValue(potion);
            System.out.printf("%d. %s (sell %d)%n", i + 1, potion.getName(), price);
        }
        System.out.println("0. Cancel");
        int pick = promptIntInRange(scanner, "Sell which potion: ", 0, hero.getPotions().size());
        if (pick == 0) {
            return;
        }
        Potion sold = hero.removePotion(pick - 1);
        int price = resaleValue(sold);
        hero.addMoney(price);
        System.out.printf("Sold %s for %d gold.%n", sold.getName(), price);
    }

    private Hero chooseHero(Scanner scanner, List<Hero> party) {
        System.out.println();
        System.out.println("Choose hero to trade with:");
        for (int i = 0; i < party.size(); i++) {
            Hero hero = party.get(i);
            System.out.printf("%d. %s (%s, Gold %d)%n", i + 1, hero.getName(), hero.getHeroType(), hero.getMoney());
        }
        System.out.println("0. Leave market");
        int pick = promptIntInRange(scanner, "Hero: ", 0, party.size());
        if (pick == 0) {
            return null;
        }
        return party.get(pick - 1);
    }

    private int resaleValue(Item item) {
        return Math.max(1, item.getCost() / 2);
    }

    private int promptIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                return min;
            }
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.printf("Please enter a number from %d to %d.%n", min, max);
        }
    }

    private String weaponLabel(Weapon weapon) {
        return String.format("%s (cost %d, lvl %d, dmg %d, hands %d)",
                weapon.getName(),
                weapon.getCost(),
                weapon.getRequiredLevel(),
                weapon.getDamage(),
                weapon.getRequiredHands());
    }

    private String armorLabel(Armor armor) {
        return String.format("%s (cost %d, lvl %d, reduction %d)",
                armor.getName(),
                armor.getCost(),
                armor.getRequiredLevel(),
                armor.getDamageReduction());
    }

    private String spellLabel(Spell spell) {
        return String.format("%s (cost %d, lvl %d, dmg %d, mana %d, %s)",
                spell.getName(),
                spell.getCost(),
                spell.getRequiredLevel(),
                spell.getDamage(),
                spell.getManaCost(),
                spell.getSchool());
    }

    private String potionLabel(Potion potion) {
        return String.format("%s (cost %d, lvl %d, +%d %s)",
                potion.getName(),
                potion.getCost(),
                potion.getRequiredLevel(),
                potion.getIncreaseAmount(),
                potion.getEffect());
    }

    private static List<Weapon> parseWeapons(Path filePath) throws IOException {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.skip(1)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> line.split("\\s+"))
                    .map(parts -> new Weapon(
                            parts[0],
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4])))
                    .toList();
        }
    }

    private static List<Armor> parseArmors(Path filePath) throws IOException {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.skip(1)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> line.split("\\s+"))
                    .map(parts -> new Armor(
                            parts[0],
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3])))
                    .toList();
        }
    }

    private static List<Spell> parseSpells(Path filePath, SpellSchool school) throws IOException {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.skip(1)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> line.split("\\s+"))
                    .map(parts -> new Spell(
                            parts[0],
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4]),
                            school))
                    .toList();
        }
    }

    private static List<Potion> parsePotions(Path filePath) throws IOException {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.skip(1)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(Market::parsePotion)
                    .toList();
        }
    }

    private static Potion parsePotion(String line) {
        String[] parts = line.split("\\s+");
        String name = parts[0];
        int cost = Integer.parseInt(parts[1]);
        int level = Integer.parseInt(parts[2]);
        int increase = Integer.parseInt(parts[3]);

        // Potion effect text can have multiple words; rebuild it before classification.
        StringBuilder effectText = new StringBuilder();
        for (int i = 4; i < parts.length; i++) {
            if (i > 4) {
                effectText.append(' ');
            }
            effectText.append(parts[i]);
        }
        PotionEffect effect = parsePotionEffect(effectText.toString());
        return new Potion(name, cost, level, increase, effect);
    }

    private static PotionEffect parsePotionEffect(String text) {
        String normalized = text.trim().toLowerCase();
        if (normalized.contains("all") || normalized.contains("/")) {
            return PotionEffect.ALL;
        }
        if (normalized.contains("health")) {
            return PotionEffect.HEALTH;
        }
        if (normalized.contains("mana")) {
            return PotionEffect.MANA;
        }
        if (normalized.contains("strength")) {
            return PotionEffect.STRENGTH;
        }
        if (normalized.contains("defense")) {
            return PotionEffect.DEFENSE;
        }
        if (normalized.contains("agility") || normalized.contains("dexterity")) {
            return PotionEffect.AGILITY;
        }
        return PotionEffect.ALL;
    }
}
