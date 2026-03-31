package rpg.util;

import java.util.Random;

// Default dice implementation backed by java.util.Random.
public class RandomDice implements Dice {
    private final Random random;

    public RandomDice() {
        this(new Random());
    }

    public RandomDice(Random random) {
        this.random = random;
    }

    @Override
    public int roll(int sides) {
        if (sides < 1) {
            throw new IllegalArgumentException("sides must be >= 1");
        }
        return random.nextInt(sides) + 1;
    }
}
