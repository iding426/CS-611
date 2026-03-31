package rpg.encounter;

import java.util.List;

import rpg.character.Monster;

// Simple battle payload: the monster list for one encounter.
public record Encounter(List<Monster> monsters) {
    public Encounter {
        if (monsters == null || monsters.isEmpty()) {
            throw new IllegalArgumentException("encounter must include monsters");
        }
    }
}
