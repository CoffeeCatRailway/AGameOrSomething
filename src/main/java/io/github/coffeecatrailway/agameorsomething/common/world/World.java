package io.github.coffeecatrailway.agameorsomething.common.world;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.Shader;
import io.github.coffeecatrailway.agameorsomething.common.io.InputHandler;
import io.github.coffeecatrailway.agameorsomething.common.io.Window;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.RoundingMode;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public class World
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Comparator<Vector2ic> POS_COMPARATOR = (pos1, pos2) -> {
        int r = Integer.compare(pos1.y(), pos2.y()) * -1;
        if (r == 0 && !pos1.equals(pos2))
            r = Integer.compare(pos1.x(), pos2.x());
        return r;
    };
    private final SortedMap<Vector2ic, Tile> tilesBg;
    private final SortedMap<Vector2ic, Tile> tilesFg;

    public World()
    {
        this.tilesBg = new TreeMap<>(POS_COMPARATOR);
        this.tilesFg = new TreeMap<>(POS_COMPARATOR);
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
        if (Window.getInputHandler().isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT))
            this.setTile(InputHandler.getMousePosInWorldSpace(something.getCamera()).div(2f).add(.5f, .5f).get(RoundingMode.FLOOR, new Vector2i()), TileRegistry.TEST.get(), true);
    }

    public void render(AGameOrSomething something, Camera camera)
    {
        Timer.start("tileRendering"); //TODO: Fix lag spike, check is background tile is visible
        this.tilesBg.entrySet().stream().filter(entry -> this.isPosInView(entry.getKey(), camera)).forEach((entry) -> something.getTileRenderer().renderOnGrid(entry.getValue(), entry.getKey(), Shader.TILE_BASIC, camera));
        this.tilesFg.entrySet().stream().filter(entry -> this.isPosInView(entry.getKey(), camera)).forEach((entry) -> something.getTileRenderer().renderOnGrid(entry.getValue(), entry.getKey(), Shader.TILE_BASIC, camera));
        Timer.end("tileRendering", LOGGER);

//        something.getTileRenderer().renderOffGrid(TileRegistry.SAND.get(), InputHandler.getMousePosInWorldSpace(camera), Shader.TILE_BASIC, camera);
//        Vector2i screenPosGrid = InputHandler.getMousePosInWorldSpace(camera).div(2f).add(.5f, .5f).get(RoundingMode.FLOOR, new Vector2i());
//        something.getTileRenderer().renderOnGrid(TileRegistry.SAND.get(), screenPosGrid, Shader.TILE_BASIC, camera);
    }

    /**
     * @param pos {@link Vector2ic} - Tile based position
     * @param camera {@link Camera} - Main world camera
     * @return True if pos is within view on screen
     */
    public boolean isPosInView(Vector2ic pos, Camera camera)
    {
        float viewWidth = AGameOrSomething.getInstance().getWindow().getWidth() / 4f / camera.getScale() + 2f;
        float viewHeight = AGameOrSomething.getInstance().getWindow().getHeight() / 4f / camera.getScale() + 2f;
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
        if (foreground)
            return this.tilesFg.put(pos, tile);
        return this.tilesBg.put(pos, tile);
    }
}
