package io.github.coffeecatrailway.agameorsomething.common.world;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.common.io.Window;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.common.utils.TilePos;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public class TestWorld extends AbstractWorld
{
    private static final Logger LOGGER = LogManager.getLogger();

    public TestWorld()
    {
        super(40);
        this.generate();
    }

    @Override
    public void generate()
    {
        Timer.start("generateWorld");
        boolean borderFlag = false;
        for (int y = -this.worldRadius; y < this.worldRadius + 1; y++)
        {
            for (int x = -this.worldRadius; x < this.worldRadius + 1; x++)
            {
                Vector2i pos = new Vector2i(x, y);
                Tile tile = TileRegistry.GRASS.get();
                borderFlag = pos.x == this.worldRadius || pos.x == -this.worldRadius || pos.y == this.worldRadius || pos.y == -this.worldRadius;
                if (pos.distance(0, 0) < 4 || borderFlag)
                    tile = TileRegistry.DIRT.get();
                this.tilesBg.put(pos, tile);
                this.tilesFg.put(pos, borderFlag ? tile : TileRegistry.AIR.get());
            }
        }

        this.tilesBg.put(new Vector2i(3, 2), TileRegistry.SAND.get());
        LOGGER.debug("World generated in {}ms", Timer.end("generateWorld"));
    }

    @Override
    public void tick(AGameOrSomething something, Camera camera)
    {
        super.tick(something, camera);
        if (Window.getInputHandler().isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT))
    }

    @Override
    public void render(AGameOrSomething something, Camera camera)
    {
        Timer.start("tileRendering"); //TODO: Fix lag spike, check if background tile is visible
        this.tilesBg.entrySet().stream().filter(entry -> entry.getValue().isVisible() && this.isPosInView(entry.getKey(), something.getWindow(), camera)).forEach((entry) -> something.getTileRenderer().renderOnGrid(entry.getValue(), entry.getKey(), Shader.SIMPLE, camera));
        this.tilesFg.entrySet().stream().filter(entry -> entry.getValue().isVisible() && this.isPosInView(entry.getKey(), something.getWindow(), camera)).forEach((entry) -> something.getTileRenderer().renderOnGrid(entry.getValue(), entry.getKey(), Shader.SIMPLE, camera));
        long millis = Timer.end("tileRendering");
        if (millis >= 30L)
            LOGGER.warn("Tile rendering took {}ms", millis);

//        something.getTileRenderer().renderOffGrid(TileRegistry.SAND.get(), InputHandler.getMousePosInWorldSpace(camera), Shader.TILE_BASIC, camera);
//        Vector2i screenPosGrid = InputHandler.getMousePosInWorldSpace(camera).div(2f).add(.5f, .5f).get(RoundingMode.FLOOR, new Vector2i());
//        something.getTileRenderer().renderOnGrid(TileRegistry.SAND.get(), screenPosGrid, Shader.TILE_BASIC, camera);
        {
            TilePos pos = camera.trace(this);
            if (pos != TilePos.EMPTY)
                this.setTile(pos.pos(), TileRegistry.SAND.get(), true);
        } else if (Window.getInputHandler().isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT))
        {
            TilePos pos = camera.trace(this);
            if (pos != TilePos.EMPTY)
                this.setTile(pos.pos(), TileRegistry.DIRT.get(), true);
        } else if (Window.getInputHandler().isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_MIDDLE))
        {
            TilePos pos = camera.trace(this);
            if (pos != TilePos.EMPTY)
                this.setTile(pos.pos(), TileRegistry.AIR.get(), true);
        }
    }
}
