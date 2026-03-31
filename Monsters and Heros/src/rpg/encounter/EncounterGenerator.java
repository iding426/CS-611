package rpg.encounter;

public interface EncounterGenerator {
    Encounter generateEncounter(int heroLevel, int monsterCount);
}
