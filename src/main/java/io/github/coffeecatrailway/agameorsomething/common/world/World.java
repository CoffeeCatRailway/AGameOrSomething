package io.github.coffeecatrailway.agameorsomething.common.world;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.Shader;
import io.github.coffeecatrailway.agameorsomething.common.io.InputHandler;
import io.github.coffeecatrailway.agameorsomething.common.io.Window;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import org.agrona.collections.Object2ObjectHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.RoundingMode;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFW;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public class World
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final Object2ObjectHashMap<Vector2ic, Tile> tilesBg;
    private final Object2ObjectHashMap<Vector2ic, Tile> tilesFg;

    private int renderDistance = 64;

    public World()
    {
        this.tilesBg = new Object2ObjectHashMap<>();
        this.tilesFg = new Object2ObjectHashMap<>();
        int startRadius = 30;
        for (int y = -startRadius; y < startRadius + 1; y++)
        {
            for (int x = -startRadius; x < startRadius + 1; x++)
            {
                Vector2i pos = new Vector2i(x, y);
                Tile tile = TileRegistry.GRASS.get();
                if (pos.distance(0, 0) < 4)
                    tile = TileRegistry.DIRT.get();
                this.tilesBg.put(pos, tile);
                this.tilesFg.put(pos, TileRegistry.AIR.get());
            }
        }

        this.tilesBg.put(new Vector2i(3, 2), TileRegistry.SAND.get());
    }

    public void tick(AGameOrSomething something)
    {
    }

    public void render(AGameOrSomething something, Camera camera)
    {
//        Timer.start("tileRendering"); TODO: Fix lag spike
        int posX = (int) (camera.getPosition().x / (camera.getScale() / 2f));
        int posY = (int) (camera.getPosition().y / (camera.getScale() / 2f));
        for (int i = 0; i < this.renderDistance; i++)
        {
            for (int j = 0; j < this.renderDistance; j++)
            {
                Vector2i pos = new Vector2i(i - posX - (this.renderDistance / 2) + 1, j + posY - (this.renderDistance / 2) + 1);
                Tile tileBg = this.getTile(pos, false);
                if (tileBg != null)
                    something.getTileRenderer().renderOnGrid(tileBg, pos, Shader.TILE_BASIC, camera);
                Tile tileFg = this.getTile(pos, true);
                if (tileFg != null)
                    something.getTileRenderer().renderOnGrid(tileFg, pos, Shader.TILE_BASIC, camera);
            }
        }
//        Timer.end("tileRendering", LOGGER);
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
}
