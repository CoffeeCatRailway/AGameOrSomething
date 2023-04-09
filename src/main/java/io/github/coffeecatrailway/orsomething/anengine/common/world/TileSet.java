package io.github.coffeecatrailway.orsomething.anengine.common.world;

import io.github.coffeecatrailway.orsomething.anengine.common.collision.BoundingBox;
import io.github.coffeecatrailway.orsomething.anengine.common.tile.Tile;
import io.github.coffeecatrailway.orsomething.anengine.core.registry.TileRegistry;
import org.joml.Vector2f;
import org.joml.Vector2ic;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author CoffeeCatRailway
 * Created: 25/12/2022
 */
public class TileSet
{
    private final Map<Vector2ic, Tile> tileSet; // TODO: Convert to chunk based system, Use arrays[] instead of Maps
    private final Map<Vector2ic, BoundingBox> boundingBoxes = new HashMap<>();
    private boolean boundsEnabled = true;

    public TileSet()
    {
        this(new HashMap<>());
    }

    public TileSet(Map<Vector2ic, Tile> tileSet)
    {
        this.tileSet = tileSet;
    }

    public TileSet disableBounds()
    {
        this.boundsEnabled = false;
        return this;
    }

    public Stream<Map.Entry<Vector2ic, Tile>> entryStream()
    {
        return this.tileSet.entrySet().stream();
    }

    public Tile getTile(Vector2ic pos)
    {
        return this.tileSet.getOrDefault(pos, TileRegistry.AIR.get());
    }

    public Tile setTile(Vector2ic pos, Tile tile)
    {
        if (this.boundsEnabled)
        {
            if (tile.isCollidable())
                this.boundingBoxes.put(pos, new BoundingBox(new Vector2f(pos), tile.getBounds()));
            else
                this.boundingBoxes.put(pos, BoundingBox.EMPTY);
        }
        return this.tileSet.put(pos, tile);
    }

    public BoundingBox getBounds(Vector2ic pos)
    {
        return this.boundingBoxes.getOrDefault(pos, BoundingBox.EMPTY);
    }

    public enum Level
    {
        BACKGROUND, MIDGROUND, FOREGROUND
    }
}
