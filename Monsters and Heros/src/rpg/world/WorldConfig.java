package rpg.world;

// Immutable world setup values used when generating the map and encounter rules.
public record WorldConfig(
        int size,
        double inaccessibleRatio,
        double marketRatio,
        double commonRatio,
    double encounterChance) {

    public WorldConfig {
        // Validate all config values early so map generation logic can assume correctness.
        if (size < 2) {
            throw new IllegalArgumentException("size must be >= 2");
        }
        if (inaccessibleRatio < 0 || marketRatio < 0 || commonRatio < 0) {
            throw new IllegalArgumentException("tile ratios must be non-negative");
        }
        double ratioSum = inaccessibleRatio + marketRatio + commonRatio;
        if (Math.abs(ratioSum - 1.0) > 0.0001) {
            throw new IllegalArgumentException("tile ratios must sum to 1.0");
        }
        if (encounterChance < 0 || encounterChance > 1) {
            throw new IllegalArgumentException("encounterChance must be in [0,1]");
        }
    }

    // Assignment-style default world: 8x8 with 20/30/50 tile split.
    public static WorldConfig defaultConfig() {
        return new WorldConfig(
                8,
                0.20,
                0.30,
                0.50,
                0.35);
    }
}
