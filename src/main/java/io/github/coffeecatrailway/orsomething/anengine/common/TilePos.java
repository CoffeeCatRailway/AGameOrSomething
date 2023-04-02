package io.github.coffeecatrailway.orsomething.anengine.common;

import io.github.coffeecatrailway.orsomething.anengine.common.world.TileSet;
import org.joml.Vector2f;
import org.joml.Vector2i;

/**
 * @author Ocelot
 * Created: 13/11/2022
 */
public record TilePos(Vector2f intersect, Vector2i pos, TileSet.Level level)
{
    public static final TilePos EMPTY = new TilePos(new Vector2f(), new Vector2i(), TileSet.Level.BACKGROUND);
}
