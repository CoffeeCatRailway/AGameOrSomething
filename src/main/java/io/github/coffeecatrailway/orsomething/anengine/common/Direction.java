package io.github.coffeecatrailway.orsomething.anengine.common;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public enum Direction
{
    NORTH, EAST, SOUTH, WEST;

    public Direction clockwise()
    {
        return switch (this){
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }

    public Direction antiClockwise()
    {
        return switch (this){
            case NORTH -> WEST;
            case EAST -> NORTH;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
        };
    }
}
