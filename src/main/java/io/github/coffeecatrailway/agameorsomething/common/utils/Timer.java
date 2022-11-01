package io.github.coffeecatrailway.agameorsomething.common.utils;

import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CoffeeCatRailway
 * Created: 18/07/2022
 */
public final class Timer
{
    private static final Map<String, Instant> TIMERS = new HashMap<>();

    private Timer() {}

    public static void start(String name)
    {
        TIMERS.put(name, Instant.now());
    }

    public static void end(String name, Logger logger)
    {
        Instant end = Instant.now();
        logger.debug("Time elapsed for `{}`: {}", name, Duration.between(TIMERS.get(name), end).toMillis());
        TIMERS.remove(name);
    }

    public static double getTimeInSeconds()
    {
        return (double) System.nanoTime() / (double) 1000000000L;
    }
}
