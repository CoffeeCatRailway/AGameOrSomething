package io.github.coffeecatrailway.orsomething.anengine.common;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author CoffeeCatRailway
 * Created: 18/07/2022
 */
public final class Timer
{
    private static final Map<String, Instant> TIMERS = new HashMap<>();

    private Timer() {}

    /**
     * Starts a timer
     * @param name Name of the timer
     */
    public static void start(String name)
    {
        TIMERS.put(timerName(name), Instant.now());
    }

    /**
     * @param name   Name of the timer
     * @return Milliseconds elapsed
     */
    public static long end(String name)
    {
        Instant end = Instant.now();
        return Duration.between(TIMERS.remove(timerName(name)), end).toMillis();
    }

    private static String timerName(String name)
    {
        return name.toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");
    }

    public static double getTimeInSeconds()
    {
        return (double) System.nanoTime() / (double) 1_000_000_000L;
    }
}
