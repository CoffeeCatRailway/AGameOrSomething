package io.github.coffeecatrailway.agameorsomething.common.world;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.common.entity.WanderingEntity;
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

//    private BoundingBox box1, box2;

    public TestWorld()
    {
        super(40);
        this.generate();

//        this.box1 = new BoundingBox(new Vector2f(1f, 0f), 1f, 1f);
//        this.box2 = new BoundingBox(new Vector2f(0f), 1f, 1.5f);
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
                    this.setTile(pos, TileRegistry.SAND.get(), false);
                    this.setTile(pos, TileRegistry.DIRT.get(), true);
                } else
                    this.setTile(pos, (pos.distance(0, 0) < 4 ? TileRegistry.DIRT.get() : TileRegistry.GRASS.get()), false);
            }
        }

        this.setTile(new Vector2i(1, 0), TileRegistry.SAND.get(), true);
        this.setTile(new Vector2i(-1, 0), TileRegistry.SAND.get(), true);

        WanderingEntity wanderer = new WanderingEntity();
        wanderer.getPosition().set(-10f, 10f);
        this.addEntity(wanderer);

        WanderingEntity wanderer1 = new WanderingEntity();
        wanderer1.getPosition().set(10f, 10f);
        this.addEntity(wanderer1);
        LOGGER.debug("World generated in {}ms", Timer.end("generateWorld"));
    }

    @Override
    public void tick(float delta, AGameOrSomething something, Camera camera)
    {
        super.tick(delta, something, camera);
//        this.box2.getPosition().set(this.player.getPosition());
//        if (this.box2.isIntersecting(this.box1))
//            this.box2.correctPosition(this.box1, this.player.getPosition());

        Tile tile = null;

        MouseHandler mouseHandler = something.getMouseHandler();
        if (mouseHandler.isButtonPressed(GLFW_MOUSE_BUTTON_LEFT))
            tile = TileRegistry.SAND.get();
        else if (mouseHandler.isButtonPressed(GLFW_MOUSE_BUTTON_RIGHT))
            tile = TileRegistry.DIRT.get();
        else if (mouseHandler.isButtonPressed(GLFW_MOUSE_BUTTON_MIDDLE))
            tile = TileRegistry.AIR.get();

        if (tile != null)
        {
            TilePos pos = camera.trace(this);
            if (pos != TilePos.EMPTY)
                this.setTile(pos.pos(), tile, true);
        }
    }
}
