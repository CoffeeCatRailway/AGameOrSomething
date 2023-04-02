package io.github.coffeecatrailway.orsomething.anengine.common;

/**
 * @author CoffeeCatRailway
 * Created: 02/04/2023
 */
public class MutableBoolean
{
    private boolean value;

    public MutableBoolean(boolean value)
    {
        this.value = value;
    }

    public boolean get()
    {
        return this.value;
    }

    public void set(boolean value)
    {
        this.value = value;
    }
}
