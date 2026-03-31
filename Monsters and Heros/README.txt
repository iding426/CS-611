Legends: Monsters and Heroes - Simple File Guide

This file explains what each Java code file does in very simple terms.

Main Code Entry
- Main.java: Starts the game, loads data files, creates the game controller, and runs the game.
- GameController.java: Main game loop and player interaction (movement, battle turns, market entry, and party setup).

Character Files
- CharacterUnit.java: Base class for any character with a name and level.
- Hero.java: Base hero class with stats, inventory, equipment, money, and hero combat calculations.
- Warrior.java: Hero type focused on strength and agility growth.
- Sorcerer.java: Hero type focused on dexterity and agility growth.
- Paladin.java: Hero type focused on strength and dexterity growth.
- Monster.java: Monster class with damage, defense, and dodge values.

Encounter Files
- Encounter.java: Holds the monsters in one battle encounter.
- EncounterGenerator.java: Interface for encounter generation.
- RandomMonsterGenerator.java: Creates random monsters for encounters based on hero level and party size.
- MonsterRepository.java: Loads and stores monster templates from the text data files.

Item Files
- Item.java: Base class for all buyable/usable items.
- Weapon.java: Weapon item with damage and required hands.
- Armor.java: Armor item with damage reduction.
- Spell.java: Spell item with damage, mana cost, and school.
- SpellSchool.java: Enum for spell types (fire, ice, lightning).
- Potion.java: Potion item with stat effect and amount.
- PotionEffect.java: Enum for potion effect types.

Utility Files
- Dice.java: Interface for dice/random rolls.
- RandomDice.java: Standard random dice implementation.

World / Map Files
- WorldConfig.java: Settings for map size, tile ratios, and encounter chance.
- WorldMap.java: Builds the map, tracks party position, handles movement, map rendering, and encounter checks.
- Tile.java: Base class for map tiles.
- CommonTile.java: Normal tile where battles can happen.
- MarketTile.java: Tile where players can open the market.
- InaccessibleTile.java: Blocked tile the party cannot enter.
- TileType.java: Enum for tile categories.
- Direction.java: Enum for movement directions.
- MoveResult.java: Enum describing move outcomes (moved or blocked).
- Position.java: Row/column coordinate object.
- Market.java: Handles market menus, buying, selling, and loading market inventory from text files.

Given Data Folder
- Legends_Monsters_and_Heroes/: Text files for heroes, monsters, spells, weapons, armor, and potions used by the game.

OO Design Principles Used
- Encapsulation: Character and item data is kept in classes with private fields and controlled methods (for example in Hero.java and Item.java).
- Inheritance: Shared parent classes are used to avoid repeated logic (CharacterUnit.java -> Hero.java/Monster.java, Tile.java -> CommonTile.java/MarketTile.java/InaccessibleTile.java).
- Abstraction: Abstract classes and interfaces define common behavior without exposing every detail (Hero.java is abstract, Dice.java and EncounterGenerator.java are interfaces).
- Polymorphism: Different hero types (Warrior.java, Sorcerer.java, Paladin.java) share the same Hero base type but apply different growth behavior.

How to Compile and Run
1. Open a terminal in the project folder: /Users/ianding/Documents/CS 611/Monsters and Heros
2. Compile:
	 javac -d out $(find src -name '*.java')
3. Run:
	 java -cp out rpg.Main

Optional Run Command (explicit data folder)
- java -cp out rpg.Main "/Users/ianding/Documents/CS 611/Monsters and Heros/Legends_Monsters_and_Heroes"

Sample Interaction
- Start the game
	- Select party setup:
		- 1 for default party, or
		- 2 to create your own heroes
- Map appears
	- Red X = inaccessible tile
	- Yellow M = market tile
	- Green P = your current tile
- Movement example
	- Enter: d
	- Result: party moves right if tile is accessible
- Enter market example (only on market tile)
	- Enter: m
	- Choose hero, then buy/sell categories
- Battle example (if encounter triggers)
	- For each hero turn choose:
		- 1 Attack
		- 2 Cast Spell
		- 3 Use Potion
		- 4 Equip Weapon/Armor
- Quit game
	- Enter: q
