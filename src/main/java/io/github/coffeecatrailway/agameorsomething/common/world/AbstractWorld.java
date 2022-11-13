package io.github.coffeecatrailway.agameorsomething.common.world;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.common.io.Window;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * @author CoffeeCatRailway
 * Created: 11/11/2022
 */
public abstract class AbstractWorld implements World
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Comparator<Vector2ic> POS_COMPARATOR = (pos1, pos2) -> {
        int r = Integer.compare(pos1.y(), pos2.y()) * -1;
        if (r == 0 && !pos1.equals(pos2))
            r = Integer.compare(pos1.x(), pos2.x());
        return r;
    };

    protected final TreeMap<Vector2ic, Tile> tilesBg; // TODO: Convert to chunk based system
    protected final TreeMap<Vector2ic, Tile> tilesFg;

    public static final int MIN_WORLD_RADIUS = 10;
    protected final int worldRadius; // Distance from 0,0 to each edge
    protected final int worldSize; // Width & height of the world

    public AbstractWorld(int worldRadius)
    {
        if (worldRadius < MIN_WORLD_RADIUS)
            worldRadius = MIN_WORLD_RADIUS;
        this.worldRadius = worldRadius;
        this.worldSize = this.worldRadius * 2 + 1;

        this.tilesBg = new TreeMap<>(POS_COMPARATOR);
        this.tilesFg = new TreeMap<>(POS_COMPARATOR);
    }

    @Override
    public void tick(AGameOrSomething something, Camera camera)
    {
    }

    public abstract void render(AGameOrSomething something, Camera camera);

    /**
     * @param pos    {@link Vector2ic} - Tile based position
     * @param window {@link Window} - Application window
     * @param camera {@link Camera} - Main game camera
     * @return True if pos is within view on screen
     */
    public boolean isPosInView(Vector2ic pos, Window window, Camera camera)
    {
        float viewWidth = window.getWidth() / 4f / camera.getScale() + 2f;
        float viewHeight = window.getHeight() / 4f / camera.getScale() + 2f;
        Vector2i worldPos = pos.add((int) (camera.getPosition().x() / 2f), (int) (-camera.getPosition().y() / 2f), new Vector2i());
        return worldPos.x() > -viewWidth && worldPos.x() < viewWidth && worldPos.y() > -viewHeight && worldPos.y() < viewHeight;
    }

    public Tile getTile(Vector2ic pos, boolean foreground)
    {
        Tile tile = foreground ? this.tilesFg.get(pos) : this.tilesBg.get(pos);
        return tile == null ? TileRegistry.AIR.get() : tile;
    }

    public Tile setTile(Vector2ic pos, Tile tile, boolean foreground)
    {
        return this.setTile(pos, tile, foreground, false);
    }

    public Tile setTile(Vector2ic pos, Tile tile, boolean foreground, boolean force)
    {
        if (force || !this.getTile(pos, foreground).equals(tile))
        {
            if (foreground)
                return this.tilesFg.put(pos, tile);
            return this.tilesBg.put(pos, tile);
        }
        return TileRegistry.AIR.get();
    }

    public int getWorldRadius()
    {
        return this.worldRadius;
    }

    public int getWorldSize()
    {
        return this.worldSize;
    }
}
