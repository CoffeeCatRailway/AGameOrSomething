package io.github.coffeecatrailway.agameorsomething.common.utils;

import org.joml.Vector2f;
import org.joml.Vector2i;

/**
 * @author Ocelot
 * Created: 13/11/2022
 */
public record TilePos(Vector2f intersect, Vector2i pos, boolean foreground)
{
    public static final TilePos EMPTY = new TilePos(new Vector2f(), new Vector2i(), false);
}
