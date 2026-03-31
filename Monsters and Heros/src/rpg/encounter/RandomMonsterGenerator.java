package rpg.encounter;

import java.util.ArrayList;
import java.util.List;

import rpg.character.Monster;
import rpg.encounter.MonsterRepository.MonsterTemplate;
import rpg.util.Dice;

public class RandomMonsterGenerator implements EncounterGenerator {
    private final MonsterRepository repository;
    private final Dice dice;

    public RandomMonsterGenerator(MonsterRepository repository, Dice dice) {
        this.repository = repository;
        this.dice = dice;
    }

    @Override
    // Builds one encounter by sampling monsters whose level fits the hero team.
    public Encounter generateEncounter(int heroLevel, int monsterCount) {
        if (monsterCount < 1) {
            throw new IllegalArgumentException("monsterCount must be >= 1");
        }

        List<MonsterTemplate> candidates = repository.getTemplatesUpToLevel(heroLevel);
        List<Monster> monsters = new ArrayList<>();
        for (int i = 0; i < monsterCount; i++) {
            int index = dice.roll(candidates.size()) - 1;
            monsters.add(candidates.get(index).toMonster());
        }
        return new Encounter(monsters);
    }
}
