package io.github.coffeecatrailway.agameorsomething.common.utils;

import java.util.Random;
import java.util.UUID;

/**
 * @author CoffeeCatRailway
 * Created: 14/12/2022
 */
public class MatUtils
{
    private static final Random RANDOM = new Random(41L);

    public static UUID insecureUUID()
    {
        return insecureUUID(RANDOM);
    }

    public static UUID insecureUUID(Random random)
    {
        long most = random.nextLong() & -61441L | 16384L;
        long least = random.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
        return new UUID(most, least);
    }

    public static float randomFloat(float min, float max)
    {
        return randomFloat(RANDOM, min, max);
    }

    public static float randomFloat(Random random, float min, float max)
    {
        return random.nextFloat() * (max - min) + min;
    }
}
