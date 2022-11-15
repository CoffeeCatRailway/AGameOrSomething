package io.github.coffeecatrailway.agameorsomething.common.world;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.Shader;
import io.github.coffeecatrailway.agameorsomething.common.io.Window;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
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

    private static final Vector2i IN_VIEW_POS = new Vector2i();

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


    /**
     * @param pos    {@link Vector2ic} - Tile based position
     * @param window {@link Window} - Application window
     * @param camera {@link Camera} - Main game camera
     * @return True if intersect is within view on screen
     */
    public boolean isPosInView(Vector2ic pos, Window window, Camera camera)
    {
        int viewWidth = (int) (window.getWidth() / 4f / camera.getZoom()) + 5;
        int viewHeight = (int) (window.getHeight() / 4f / camera.getZoom()) + 5;
        pos.add((int) -camera.getPosition().x(), (int) -camera.getPosition().y(), IN_VIEW_POS);
        return IN_VIEW_POS.x() > -viewWidth && IN_VIEW_POS.x() < viewWidth && IN_VIEW_POS.y() > -viewHeight && IN_VIEW_POS.y() < viewHeight;
//        Vector4f p = new Vector4f(1.0F).mul(camera.getProjectionMatrix()).mul(camera.getViewMatrix()).mul(pos.x(), pos.y(), 0.0F, 1.0F);
//        return p.lengthSquared() < 1;
    }

    @Override
    public Tile getTile(Vector2ic pos, boolean foreground)
    {
        Tile tile = foreground ? this.tilesFg.get(pos) : this.tilesBg.get(pos);
        return tile == null ? TileRegistry.AIR.get() : tile;
    }

    @Override
    public Tile setTile(Vector2ic pos, Tile tile, boolean foreground, boolean force)
    {
        if (pos.x() > this.worldRadius || pos.x() < -this.worldRadius || pos.y() > this.worldRadius || pos.y() < -this.worldRadius)
        {
            LOGGER.warn("Tile {} was placed outside of world at position {}", tile.getObjectId(), pos);
            return TileRegistry.AIR.get();
        }
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