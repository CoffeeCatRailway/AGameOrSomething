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
    public void tick(float delta, AGameOrSomething something, Camera camera)
    {
        super.tick(delta, something, camera);

        Tile tile = null;

        if (Window.getInputHandler().isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT))
            tile = TileRegistry.SAND.get();
        else if (Window.getInputHandler().isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT))
            tile = TileRegistry.DIRT.get();
        else if (Window.getInputHandler().isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_MIDDLE))
            tile = TileRegistry.AIR.get();

        if (tile != null)
        {
            TilePos pos = camera.trace(this);
            if (pos != TilePos.EMPTY)
                this.setTile(pos.pos(), tile, true);
        }
    }
}
