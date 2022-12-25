package io.github.coffeecatrailway.agameorsomething.common.world;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.agameorsomething.common.entity.PlayerEntity;
import io.github.coffeecatrailway.agameorsomething.common.entity.TestEntity;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.common.utils.TilePos;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import io.github.ocelot.window.input.MouseHandler;
import org.joml.Vector2i;
import org.slf4j.Logger;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public class TestWorld extends AbstractWorld
{
    private static final Logger LOGGER = LogUtils.getLogger();

    protected PlayerEntity player;

    public TestWorld()
    {
        super(40);
        this.generate();
    }

    @Override
    public void generate()
    {
        Timer.start("generateWorld");
        boolean borderFlag;
        for (int y = -this.worldRadius; y < this.worldRadius + 1; y++)
        {
            for (int x = -this.worldRadius; x < this.worldRadius + 1; x++)
            {
                Vector2i pos = new Vector2i(x, y);
                borderFlag = pos.x == this.worldRadius || pos.x == -this.worldRadius || pos.y == this.worldRadius || pos.y == -this.worldRadius;
                if (borderFlag)
                {
                    this.setTile(pos, TileRegistry.SAND.get(), TileSet.Level.BACKGROUND);
                    this.setTile(pos, TileRegistry.DIRT.get(), TileSet.Level.FOREGROUND);
                } else
                    this.setTile(pos, (pos.distance(0, 0) < 4 ? TileRegistry.DIRT.get() : TileRegistry.GRASS.get()), TileSet.Level.BACKGROUND);
            }
        }

        this.setTile(new Vector2i(1, 0), TileRegistry.SAND.get(), TileSet.Level.FOREGROUND);
        this.setTile(new Vector2i(-1, 0), TileRegistry.SAND.get(), TileSet.Level.FOREGROUND);

        this.player = new PlayerEntity();
        this.addEntity(this.player);

        TestEntity wanderer = new TestEntity();
        wanderer.getPosition().set(-10f, 5.5f);
        this.addEntity(wanderer);

        TestEntity wanderer1 = new TestEntity();
        wanderer1.getPosition().set(10f, 4.5f);
        this.addEntity(wanderer1);

        TestEntity wanderer2 = new TestEntity();
        wanderer2.setShouldWander(false);
        wanderer2.getPosition().set(0, -5f);
        this.addEntity(wanderer2);
        LOGGER.debug("World generated in {}ms", Timer.end("generateWorld"));
    }

    @Override
    public void tick(float delta, AGameOrSomething something)
    {
        super.tick(delta, something);

        MouseHandler mouseHandler = something.getMouseHandler();
        Tile tile = null;
        boolean middleMouse = mouseHandler.isButtonPressed(GLFW_MOUSE_BUTTON_MIDDLE);
        if (mouseHandler.isButtonPressed(GLFW_MOUSE_BUTTON_LEFT))
            tile = TileRegistry.SAND.get();
        else if (mouseHandler.isButtonPressed(GLFW_MOUSE_BUTTON_RIGHT))
            tile = TileRegistry.DIRT.get();
        else if (middleMouse)
            tile = TileRegistry.AIR.get();

        if (tile != null)
        {
            TilePos pos = something.getCamera().trace(this);
            if (pos != TilePos.EMPTY && (this.canPlaceTileAt(pos.pos(), tile, TileSet.Level.FOREGROUND) || middleMouse))
                this.setTile(pos.pos(), tile, TileSet.Level.FOREGROUND);
        }
    }
}
