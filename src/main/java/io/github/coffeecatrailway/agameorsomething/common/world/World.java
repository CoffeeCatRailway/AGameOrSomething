package io.github.coffeecatrailway.agameorsomething.common.world;

import io.github.coffeecatrailway.agameorsomething.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.Shader;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.registry.TileRegistry;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public class World
{
    private final int startRadius = 4;
    private final Map<Vector2ic, Tile> tilesBg;
    private final Map<Vector2ic, Tile> tilesFg;

    private int worldScale;
    private Matrix4f worldScaleMatrix;

    private Shader basicTileShader;

    public World()
    {
        this.tilesBg = new HashMap<>();
        this.tilesFg = new HashMap<>();
        for (int y = -this.startRadius; y < this.startRadius + 1; y++)
        {
            for (int x = -this.startRadius; x < this.startRadius + 1; x++)
            {
                Vector2i pos = new Vector2i(x, y);
                Tile tile = TileRegistry.GRASS.get();
                if (pos.distance(0, 0) < this.startRadius)
                    tile = TileRegistry.DIRT.get();
                this.tilesBg.put(pos, tile);
                this.tilesFg.put(pos, TileRegistry.AIR.get());
            }
        }
    }

    public void init()
    {
        this.worldScale = 16;
        this.worldScaleMatrix = new Matrix4f().setTranslation(new Vector3f(0f)).scale(this.worldScale);

        this.basicTileShader = new Shader("tile_basic");
    }

    public void tick()
    {

    }

    public void render(AGameOrSomething something, Camera camera)
    {
        this.tilesBg.forEach((pos, tile) -> something.getTileRenderer().render(tile, pos, this.basicTileShader, this.worldScaleMatrix, camera));
        this.tilesFg.forEach((pos, tile) -> something.getTileRenderer().render(tile, pos, this.basicTileShader, this.worldScaleMatrix, camera));
    }

    public void destroy()
    {
        this.basicTileShader.delete();
    }

    public Tile getTile(int x, int y, boolean foreground)
    {
        return this.getTile(new Vector2i(x, y), foreground);
    }

    public Tile getTile(Vector2ic pos, boolean foreground)
    {
        return foreground ? this.tilesFg.get(pos) : this.tilesBg.get(pos);
    }

    public Tile setTile(int x, int y, Tile tile,  boolean foreground)
    {
        return this.setTile(new Vector2i(x, y), tile, foreground);
    }

    public Tile setTile(Vector2ic pos, Tile tile, boolean foreground)
    {
        if (foreground)
            return this.tilesFg.put(pos, tile);
        return this.tilesBg.put(pos, tile);
    }
}
