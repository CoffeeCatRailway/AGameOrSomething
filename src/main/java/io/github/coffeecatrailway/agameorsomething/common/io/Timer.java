package io.github.coffeecatrailway.agameorsomething.common.io;

/**
 * @author CoffeeCatRailway
 * Created: 18/07/2022
 */
public class Timer
{
    public static double getTimeInSeconds()
    {
        return (double) System.nanoTime() / (double) 1000000000L;
    }
}
