package rpg;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import rpg.character.Hero;
import rpg.character.Monster;
import rpg.character.Paladin;
import rpg.character.Sorcerer;
import rpg.character.Warrior;
import rpg.encounter.Encounter;
import rpg.encounter.EncounterGenerator;
import rpg.encounter.MonsterRepository;
import rpg.encounter.RandomMonsterGenerator;
import rpg.item.Armor;
import rpg.item.Potion;
import rpg.item.PotionEffect;
import rpg.item.Spell;
import rpg.item.SpellSchool;
import rpg.item.Weapon;
import rpg.util.Dice;
import rpg.util.RandomDice;
import rpg.world.Direction;
import rpg.world.Market;
import rpg.world.MoveResult;
import rpg.world.WorldConfig;
import rpg.world.WorldMap;

public class GameController {
    private final WorldMap worldMap;
    private final List<Hero> party;
    private final EncounterGenerator encounterGenerator;
    private final Market market;
    private final Dice dice;
    private final Scanner scanner;
    private boolean partyInitialized;

    public GameController(WorldConfig worldConfig, MonsterRepository repository, Market market) {
        this.dice = new RandomDice();
        this.worldMap = new WorldMap(worldConfig, dice);
        this.party = new ArrayList<>();
        this.market = market;
        this.encounterGenerator = new RandomMonsterGenerator(repository, dice);
        this.scanner = new Scanner(System.in);
        this.partyInitialized = false;
    }

    // Main game loop for exploration commands outside battle.
    public void run() {
        printWelcome();
        // Party is selected once at startup and reused for the full session.
        initializePartyIfNeeded();
        boolean running = true;
        while (running) {
            printStatus();
            System.out.print("Enter command (w/a/s/d, m, h, q): ");
            String input = readLineOrDefault("q").trim().toLowerCase();

            switch (input) {
                case "w" -> handleMove(Direction.UP);
                case "a" -> handleMove(Direction.LEFT);
                case "s" -> handleMove(Direction.DOWN);
                case "d" -> handleMove(Direction.RIGHT);
                case "m" -> handleMarket();
                case "h" -> printHelp();
                case "q" -> running = false;
                default -> System.out.println("Unknown command. Type 'h' to view controls.");
            }
        }
        System.out.println("Game ended.");
    }

    private void handleMove(Direction direction) {
        MoveResult result = worldMap.moveParty(direction);
        switch (result) {
            case BLOCKED_BY_BOUNDARY -> System.out.println("You cannot move outside the map.");
            case BLOCKED_BY_INACCESSIBLE -> System.out.println("That tile is inaccessible.");
            case MOVED -> {
                System.out.println("Party moved " + direction.name().toLowerCase() + ".");
                worldMap.tryTriggerEncounter(highestHeroLevel(), party.size(), encounterGenerator)
                        .ifPresent(this::startBattle);
            }
        }
    }

    private void handleMarket() {
        if (!worldMap.isPartyOnMarket()) {
            System.out.println("No market here. Move onto a market tile first.");
            return;
        }
        market.openMarket(scanner, party);
    }

    private void startBattle(Encounter encounter) {
        List<Monster> monsters = encounter.monsters();
        System.out.println("Unlucky roll! Battle starts immediately.");
        System.out.println("Monsters encountered:");
        for (Monster monster : monsters) {
            System.out.printf("- %s (Level %d, Damage %d)%n",
                    monster.getName(),
                    monster.getLevel(),
                    monster.getDamage());
        }
        resolveBattle(encounter);
    }

    // Runs a full battle until one side is defeated, then applies rewards if heroes win.
    private void resolveBattle(Encounter encounter) {
        List<Monster> monsters = encounter.monsters();
        // These arrays keep temporary in-battle state without mutating base hero/monster objects.
        int[] heroHp = new int[party.size()];
        int[] heroMana = new int[party.size()];
        int[] heroAttackBonus = new int[party.size()];
        int[] heroDefenseBonus = new int[party.size()];
        int[] heroDodgeBonus = new int[party.size()];
        int[] monsterHp = new int[monsters.size()];
        int[] monsterDefensePenalty = new int[monsters.size()];
        int[] monsterDamagePenalty = new int[monsters.size()];
        int[] monsterDodgePenalty = new int[monsters.size()];

        for (int i = 0; i < party.size(); i++) {
            heroHp[i] = party.get(i).getMaxHp();
            heroMana[i] = party.get(i).getMaxMana();
        }
        for (int i = 0; i < monsters.size(); i++) {
            monsterHp[i] = monsterMaxHp(monsters.get(i));
        }

        int round = 1;
        while (hasLivingUnits(heroHp) && hasLivingUnits(monsterHp)) {
            System.out.printf("%n-- Round %d --%n", round++);

            // Hero phase: each living hero chooses one action.
            for (int heroIndex = 0; heroIndex < party.size() && hasLivingUnits(monsterHp); heroIndex++) {
                if (heroHp[heroIndex] <= 0) {
                    continue;
                }
                executeHeroTurn(
                        heroIndex,
                        heroHp,
                        heroMana,
                        heroAttackBonus,
                        heroDefenseBonus,
                        heroDodgeBonus,
                        monsters,
                        monsterHp,
                        monsterDefensePenalty,
                        monsterDamagePenalty,
                        monsterDodgePenalty);
            }

            if (!hasLivingUnits(monsterHp)) {
                break;
            }

            // Monster phase: each living monster attacks a random living hero.
            for (int monsterIndex = 0; monsterIndex < monsters.size() && hasLivingUnits(heroHp); monsterIndex++) {
                if (monsterHp[monsterIndex] <= 0) {
                    continue;
                }

                Monster monster = monsters.get(monsterIndex);
                int targetHeroIndex = randomLivingIndex(heroHp);
                Hero target = party.get(targetHeroIndex);

                int heroDodgeChance = target.calculateDodgeChance(heroDodgeBonus[targetHeroIndex]);
                if (isDodged(heroDodgeChance)) {
                    System.out.printf("%s attacks %s, but the hero dodges.%n",
                            monster.getName(),
                            target.getName());
                    continue;
                }

                int effectiveMonsterDamage = Math.max(10, monster.getDamage() - monsterDamagePenalty[monsterIndex]);
                int damage = Math.max(
                    20,
                    effectiveMonsterDamage / 6 - target.calculateDefense(heroDefenseBonus[targetHeroIndex]) / 8);
                heroHp[targetHeroIndex] = Math.max(0, heroHp[targetHeroIndex] - damage);
                System.out.printf("%s hits %s for %d damage (hero HP: %d).%n",
                        monster.getName(),
                        target.getName(),
                        damage,
                        heroHp[targetHeroIndex]);
            }
        }

        if (hasLivingUnits(heroHp)) {
            int totalMonsterLevels = monsters.stream().mapToInt(Monster::getLevel).sum();
            int expReward = totalMonsterLevels * 20;
            int goldReward = totalMonsterLevels * 30;
            for (Hero hero : party) {
                hero.addExperience(expReward);
                hero.addMoney(goldReward);
            }
            System.out.printf("Heroes won! Each hero gains %d EXP and %d gold.%n", expReward, goldReward);
        } else {
            System.out.println("Monsters won this battle. The party retreats.");
        }
    }

    // Presents one hero's turn menu and executes exactly one chosen action.
    private void executeHeroTurn(
            int heroIndex,
            int[] heroHp,
            int[] heroMana,
            int[] heroAttackBonus,
            int[] heroDefenseBonus,
            int[] heroDodgeBonus,
            List<Monster> monsters,
            int[] monsterHp,
            int[] monsterDefensePenalty,
            int[] monsterDamagePenalty,
            int[] monsterDodgePenalty) {
        Hero hero = party.get(heroIndex);

        boolean actionTaken = false;
        while (!actionTaken) {
            System.out.printf("%n%s's turn (HP %d, Mana %d).%n", hero.getName(), heroHp[heroIndex], heroMana[heroIndex]);
            System.out.println("Choose action:");
            System.out.println("1. Attack with equipped weapon");
            System.out.println("2. Cast a spell from inventory");
            System.out.println("3. Use a potion from inventory");
            System.out.println("4. Equip a weapon or armor");

            int choice = promptIntInRange("Action: ", 1, 4);
            switch (choice) {
                case 1 -> actionTaken = performAttack(heroIndex, heroAttackBonus, monsters, monsterHp, monsterDefensePenalty,
                        monsterDodgePenalty);
                case 2 -> actionTaken = performCastSpell(heroIndex, heroMana, heroAttackBonus, monsters, monsterHp,
                        monsterDefensePenalty, monsterDamagePenalty, monsterDodgePenalty);
                case 3 -> actionTaken = performUsePotion(heroIndex, heroHp, heroMana, heroAttackBonus, heroDefenseBonus,
                        heroDodgeBonus);
                case 4 -> actionTaken = performEquip(hero);
                default -> {
                }
            }
        }
    }

    private boolean performAttack(
            int heroIndex,
            int[] heroAttackBonus,
            List<Monster> monsters,
            int[] monsterHp,
            int[] monsterDefensePenalty,
            int[] monsterDodgePenalty) {
        Hero hero = party.get(heroIndex);
        int targetMonsterIndex = promptLivingMonsterIndex(monsters, monsterHp);
        Monster target = monsters.get(targetMonsterIndex);

        int effectiveMonsterDodge = Math.max(0, target.getDodgeChance() - monsterDodgePenalty[targetMonsterIndex]);
        if (isDodged(effectiveMonsterDodge)) {
            System.out.printf("%s attacks %s, but the monster dodges.%n", hero.getName(), target.getName());
            return true;
        }

        int effectiveMonsterDefense = Math.max(0, target.getDefense() - monsterDefensePenalty[targetMonsterIndex]);
        int damage = hero.calculateWeaponDamageAgainst(effectiveMonsterDefense, heroAttackBonus[heroIndex]);
        monsterHp[targetMonsterIndex] = Math.max(0, monsterHp[targetMonsterIndex] - damage);

        String weaponName = hero.getEquippedWeapon() != null ? hero.getEquippedWeapon().getName() : "bare hands";
        System.out.printf("%s attacks %s with %s for %d damage (monster HP: %d).%n",
                hero.getName(),
                target.getName(),
                weaponName,
                damage,
                monsterHp[targetMonsterIndex]);
        return true;
    }

    private boolean performCastSpell(
            int heroIndex,
            int[] heroMana,
            int[] heroAttackBonus,
            List<Monster> monsters,
            int[] monsterHp,
            int[] monsterDefensePenalty,
            int[] monsterDamagePenalty,
            int[] monsterDodgePenalty) {
        Hero hero = party.get(heroIndex);
        if (hero.getSpells().isEmpty()) {
            System.out.println("No spells in inventory.");
            return false;
        }

        System.out.println("Spells:");
        for (int i = 0; i < hero.getSpells().size(); i++) {
            Spell spell = hero.getSpells().get(i);
            System.out.printf("%d. %s (school=%s, dmg=%d, mana=%d, reqLvl=%d)%n",
                    i + 1,
                    spell.getName(),
                    spell.getSchool(),
                    spell.getDamage(),
                    spell.getManaCost(),
                    spell.getRequiredLevel());
        }

        int pick = promptIntInRange("Choose spell: ", 1, hero.getSpells().size()) - 1;
        Spell spell = hero.getSpells().get(pick);
        if (hero.getLevel() < spell.getRequiredLevel()) {
            System.out.println("Hero level is too low to cast that spell.");
            return false;
        }
        if (heroMana[heroIndex] < spell.getManaCost()) {
            System.out.println("Not enough mana for that spell.");
            return false;
        }

        int targetMonsterIndex = promptLivingMonsterIndex(monsters, monsterHp);
        Monster target = monsters.get(targetMonsterIndex);
        int effectiveMonsterDefense = Math.max(0, target.getDefense() - monsterDefensePenalty[targetMonsterIndex]);

        int damage = hero.calculateSpellDamageAgainst(spell, effectiveMonsterDefense, heroAttackBonus[heroIndex]);
        heroMana[heroIndex] -= spell.getManaCost();
        monsterHp[targetMonsterIndex] = Math.max(0, monsterHp[targetMonsterIndex] - damage);

        applySpellDebuff(spell, targetMonsterIndex, monsterDefensePenalty, monsterDamagePenalty, monsterDodgePenalty);
        System.out.printf("%s casts %s on %s for %d damage (monster HP: %d).%n",
                hero.getName(),
                spell.getName(),
                target.getName(),
                damage,
                monsterHp[targetMonsterIndex]);
        return true;
    }

    private boolean performUsePotion(
            int heroIndex,
            int[] heroHp,
            int[] heroMana,
            int[] heroAttackBonus,
            int[] heroDefenseBonus,
            int[] heroDodgeBonus) {
        Hero hero = party.get(heroIndex);
        if (hero.getPotions().isEmpty()) {
            System.out.println("No potions in inventory.");
            return false;
        }

        System.out.println("Potions:");
        for (int i = 0; i < hero.getPotions().size(); i++) {
            Potion potion = hero.getPotions().get(i);
            System.out.printf("%d. %s (%s +%d, reqLvl=%d)%n",
                    i + 1,
                    potion.getName(),
                    potion.getEffect(),
                    potion.getIncreaseAmount(),
                    potion.getRequiredLevel());
        }

        int pick = promptIntInRange("Choose potion: ", 1, hero.getPotions().size()) - 1;
        Potion potion = hero.removePotion(pick);
        if (potion == null) {
            System.out.println("Invalid potion selection.");
            return false;
        }
        if (hero.getLevel() < potion.getRequiredLevel()) {
            System.out.println("Hero level is too low to use that potion.");
            hero.addPotion(potion);
            return false;
        }

        applyPotionEffect(
                potion,
                hero,
                heroIndex,
                heroHp,
                heroMana,
                heroAttackBonus,
                heroDefenseBonus,
                heroDodgeBonus);
        System.out.printf("%s uses %s.%n", hero.getName(), potion.getName());
        return true;
    }

    private boolean performEquip(Hero hero) {
        System.out.println("Equip options: 1. Weapon  2. Armor");
        int category = promptIntInRange("Choose category: ", 1, 2);

        if (category == 1) {
            if (hero.getWeapons().isEmpty()) {
                System.out.println("No weapons available.");
                return false;
            }
            for (int i = 0; i < hero.getWeapons().size(); i++) {
                Weapon weapon = hero.getWeapons().get(i);
                System.out.printf("%d. %s (dmg=%d, reqLvl=%d)%n",
                        i + 1,
                        weapon.getName(),
                        weapon.getDamage(),
                        weapon.getRequiredLevel());
            }
            int pick = promptIntInRange("Choose weapon: ", 1, hero.getWeapons().size()) - 1;
            if (!hero.equipWeapon(pick)) {
                System.out.println("Cannot equip that weapon.");
                return false;
            }
            System.out.printf("%s equips %s.%n", hero.getName(), hero.getEquippedWeapon().getName());
            return true;
        }

        if (hero.getArmors().isEmpty()) {
            System.out.println("No armors available.");
            return false;
        }
        for (int i = 0; i < hero.getArmors().size(); i++) {
            Armor armor = hero.getArmors().get(i);
            System.out.printf("%d. %s (reduction=%d, reqLvl=%d)%n",
                    i + 1,
                    armor.getName(),
                    armor.getDamageReduction(),
                    armor.getRequiredLevel());
        }
        int pick = promptIntInRange("Choose armor: ", 1, hero.getArmors().size()) - 1;
        if (!hero.equipArmor(pick)) {
            System.out.println("Cannot equip that armor.");
            return false;
        }
        System.out.printf("%s equips %s.%n", hero.getName(), hero.getEquippedArmor().getName());
        return true;
    }

    private void applySpellDebuff(
            Spell spell,
            int targetMonsterIndex,
            int[] monsterDefensePenalty,
            int[] monsterDamagePenalty,
            int[] monsterDodgePenalty) {
        switch (spell.getSchool()) {
            case FIRE -> monsterDefensePenalty[targetMonsterIndex] += 40;
            case ICE -> monsterDamagePenalty[targetMonsterIndex] += 40;
            case LIGHTNING -> monsterDodgePenalty[targetMonsterIndex] += 12;
        }
    }

    private void applyPotionEffect(
            Potion potion,
            Hero hero,
            int heroIndex,
            int[] heroHp,
            int[] heroMana,
            int[] heroAttackBonus,
            int[] heroDefenseBonus,
            int[] heroDodgeBonus) {
        int amount = potion.getIncreaseAmount();
        switch (potion.getEffect()) {
            case HEALTH -> heroHp[heroIndex] = Math.min(hero.getMaxHp(), heroHp[heroIndex] + amount * 3);
            case MANA -> heroMana[heroIndex] = Math.min(hero.getMaxMana(), heroMana[heroIndex] + amount * 2);
            case STRENGTH -> heroAttackBonus[heroIndex] += amount;
            case AGILITY -> heroDodgeBonus[heroIndex] += Math.max(5, amount / 10);
            case DEFENSE -> heroDefenseBonus[heroIndex] += amount;
            case ALL -> {
                heroHp[heroIndex] = Math.min(hero.getMaxHp(), heroHp[heroIndex] + amount * 2);
                heroMana[heroIndex] = Math.min(hero.getMaxMana(), heroMana[heroIndex] + amount * 2);
                heroAttackBonus[heroIndex] += amount;
                heroDefenseBonus[heroIndex] += amount;
                heroDodgeBonus[heroIndex] += Math.max(5, amount / 12);
            }
        }
    }

    private int promptLivingMonsterIndex(List<Monster> monsters, int[] monsterHp) {
        System.out.println("Target monster:");
        int option = 1;
        int[] indexMap = new int[monsters.size()];
        for (int i = 0; i < monsters.size(); i++) {
            if (monsterHp[i] > 0) {
                System.out.printf("%d. %s (HP %d)%n", option, monsters.get(i).getName(), monsterHp[i]);
                indexMap[option - 1] = i;
                option++;
            }
        }
        int choice = promptIntInRange("Choose target: ", 1, option - 1);
        return indexMap[choice - 1];
    }

    private int promptIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = readLineOrDefault(String.valueOf(min)).trim();
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

    private String readLineOrDefault(String defaultValue) {
        if (!scanner.hasNextLine()) {
            return defaultValue;
        }
        return scanner.nextLine();
    }

    private int monsterMaxHp(Monster monster) {
        return 80 + monster.getLevel() * 70 + monster.getDefense() / 5;
    }

    private boolean isDodged(int dodgeChance) {
        return dice.roll(100) <= dodgeChance;
    }

    private boolean hasLivingUnits(int[] hp) {
        for (int value : hp) {
            if (value > 0) {
                return true;
            }
        }
        return false;
    }

    private int randomLivingIndex(int[] hp) {
        // Roll only among currently alive units.
        int livingCount = 0;
        for (int value : hp) {
            if (value > 0) {
                livingCount++;
            }
        }
        int pick = dice.roll(livingCount);
        int seen = 0;
        for (int i = 0; i < hp.length; i++) {
            if (hp[i] > 0) {
                seen++;
                if (seen == pick) {
                    return i;
                }
            }
        }
        throw new IllegalStateException("Failed to pick living unit");
    }

    private void printWelcome() {
        System.out.println("=== Legends: Monsters and Heroes (World of Play) ===");
        printHelp();
    }

    // Startup setup: choose either the predefined party or create 3 custom heroes.
    private void initializePartyIfNeeded() {
        if (partyInitialized) {
            return;
        }

        System.out.println();
        System.out.println("Party setup:");
        System.out.println("1. Use default party");
        System.out.println("2. Build a custom party");
        int choice = promptIntInRange("Choose setup: ", 1, 2);

        if (choice == 1) {
            createDefaultParty();
        } else {
            createCustomParty();
        }
        seedStarterInventory();
        partyInitialized = true;
    }

    // Predefined starter lineup used for quick play.
    private void createDefaultParty() {
        party.clear();
        party.add(new Warrior("Aria", 1));
        party.add(new Sorcerer("Brom", 1));
        party.add(new Paladin("Cleo", 1));
    }

    // Interactive party builder that collects names and classes for 3 heroes.
    private void createCustomParty() {
        party.clear();
        for (int i = 1; i <= 3; i++) {
            System.out.printf("%nCreate hero %d of 3%n", i);
            String name = promptHeroName("Enter hero name: ", "Hero" + i);
            Hero hero = createHeroFromChoice(name);
            party.add(hero);
        }
    }

    private Hero createHeroFromChoice(String name) {
        System.out.println("Choose class:");
        System.out.println("1. Warrior (strength/agility favored)");
        System.out.println("2. Sorcerer (dexterity/agility favored)");
        System.out.println("3. Paladin (strength/dexterity favored)");
        int typeChoice = promptIntInRange("Class: ", 1, 3);
        return switch (typeChoice) {
            case 1 -> new Warrior(name, 1);
            case 2 -> new Sorcerer(name, 1);
            case 3 -> new Paladin(name, 1);
            default -> new Warrior(name, 1);
        };
    }

    private String promptHeroName(String prompt, String defaultName) {
        System.out.print(prompt);
        String raw = readLineOrDefault(defaultName).trim();
        return raw.isEmpty() ? defaultName : raw;
    }

    private void printHelp() {
        System.out.println("Controls:");
        System.out.println("- w/a/s/d: Move up/left/down/right");
        System.out.println("- m: Enter market (only on market tile)");
        System.out.println("- h: Show help");
        System.out.println("- q: Quit");
    }

    private void printStatus() {
        System.out.println();
        System.out.println("Current map (P=party, X=inaccessible, M=market, C=common):");
        System.out.println(worldMap.renderMap());
        System.out.println("Party position: " + worldMap.getPartyPosition());
        System.out.println("Party size: " + party.size());
        System.out.println("Heroes: " + party.stream().map(h -> h.getName() + "(" + h.getHeroType() + ")").toList());
        System.out.println("Current tile: " + worldMap.getCurrentTileType());
    }

    private int highestHeroLevel() {
        int maxLevel = 1;
        for (Hero hero : party) {
            maxLevel = Math.max(maxLevel, hero.getLevel());
        }
        return maxLevel;
    }

    // Gives each hero initial gold/items/spells so battles and market are usable immediately.
    private void seedStarterInventory() {
        for (int i = 0; i < party.size(); i++) {
            Hero hero = party.get(i);
            hero.addMoney(500);
            hero.addArmor(new Armor("Platinum_Shield", 150, 1, 200));
            hero.addPotion(new Potion("Healing_Potion", 250, 1, 100, PotionEffect.HEALTH));

            if (i % 3 == 0) {
                hero.addWeapon(new Weapon("Sword", 500, 1, 800, 1));
                hero.addWeapon(new Weapon("Dagger", 200, 1, 250, 1));
                hero.addSpell(new Spell("Breath_of_Fire", 350, 1, 450, 100, SpellSchool.FIRE));
                hero.addPotion(new Potion("Strength_Potion", 200, 1, 75, PotionEffect.STRENGTH));
            } else if (i % 3 == 1) {
                hero.addWeapon(new Weapon("Dagger", 200, 1, 250, 1));
                hero.addWeapon(new Weapon("Bow", 300, 1, 500, 2));
                hero.addSpell(new Spell("Ice_Blade", 250, 1, 450, 100, SpellSchool.ICE));
                hero.addPotion(new Potion("Magic_Potion", 350, 1, 100, PotionEffect.MANA));
            } else {
                hero.addWeapon(new Weapon("Sword", 500, 1, 800, 1));
                hero.addSpell(new Spell("Lightning_Dagger", 400, 1, 500, 150, SpellSchool.LIGHTNING));
                hero.addPotion(new Potion("Mermaid_Tears", 850, 1, 100, PotionEffect.ALL));
            }
        }
    }
}
