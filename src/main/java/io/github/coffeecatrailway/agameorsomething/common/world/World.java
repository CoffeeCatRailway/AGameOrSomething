package io.github.coffeecatrailway.agameorsomething.common.world;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.Shader;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import org.agrona.collections.Object2ObjectHashMap;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3f;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public class World
{
    private final Object2ObjectHashMap<Vector2ic, Tile> tilesBg;
    private final Object2ObjectHashMap<Vector2ic, Tile> tilesFg;

    private float worldScale = 16f;
    private Matrix4f worldScaleMatrix = new Matrix4f().setTranslation(new Vector3f(0f)).scale(this.worldScale);

    public World()
    {
        this.tilesBg = new Object2ObjectHashMap<>();
        this.tilesFg = new Object2ObjectHashMap<>();
        int startRadius = 4;
        for (int y = -startRadius; y < startRadius + 1; y++)
        {
            for (int x = -startRadius; x < startRadius + 1; x++)
            {
                Vector2i pos = new Vector2i(x, y);
                Tile tile = TileRegistry.GRASS.get();
                if (pos.distance(0, 0) < startRadius)
                    tile = TileRegistry.DIRT.get();
                this.tilesBg.put(pos, tile);
                this.tilesFg.put(pos, TileRegistry.AIR.get());
            }
        }
    }

    public void tick(AGameOrSomething something)
    {
    }

    public void render(AGameOrSomething something, Camera camera)
    {
//        Instant start = Instant.now();
        this.tilesBg.forEach((pos, tile) -> something.getTileRenderer().render(tile, pos, Shader.TILE_BASIC, this.worldScaleMatrix, camera));
        this.tilesFg.forEach((pos, tile) -> something.getTileRenderer().render(tile, pos, Shader.TILE_BASIC, this.worldScaleMatrix, camera));
//        Instant end = Instant.now();
//        LOGGER.debug("Time elapsed rendering tiles: {}", Duration.between(start, end).toMillis());
    }

    public Tile getTile(int x, int y, boolean foreground)
    {
        return this.getTile(new Vector2i(x, y), foreground);
    }

    public Tile getTile(Vector2ic pos, boolean foreground)
    {
        return foreground ? this.tilesFg.get(pos) : this.tilesBg.get(pos);
    }

    public Tile setTile(int x, int y, Tile tile, boolean foreground)
    {
        return this.setTile(new Vector2i(x, y), tile, foreground);
    }

    public Tile setTile(Vector2ic pos, Tile tile, boolean foreground)
    {
        if (foreground)
            return this.tilesFg.put(pos, tile);
        return this.tilesBg.put(pos, tile);
    }

    public void destroy()
    {
    }
}
