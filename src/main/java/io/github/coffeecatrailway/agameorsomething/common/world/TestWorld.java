package io.github.coffeecatrailway.agameorsomething.common.world;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.agameorsomething.client.particle.ParticleEmitter;
import io.github.coffeecatrailway.agameorsomething.client.particle.SimpleParticleEmitter;
import io.github.coffeecatrailway.agameorsomething.client.particle.TestParticle;
import io.github.coffeecatrailway.agameorsomething.client.render.BatchRenderer;
import io.github.coffeecatrailway.agameorsomething.common.entity.PlayerEntity;
import io.github.coffeecatrailway.agameorsomething.common.entity.TestEntity;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.common.utils.MatUtils;
import io.github.coffeecatrailway.agameorsomething.common.utils.TilePos;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import io.github.ocelot.window.input.MouseHandler;
import org.joml.Vector2f;
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

    private ParticleEmitter emitter1, emitter2;

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
        wanderer.nextPos.set(2f, 5f);
        wanderer.getPosition().set(-10f, 5f);
        this.addEntity(wanderer);

        TestEntity wanderer1 = new TestEntity();
        wanderer1.nextPos.set(-2f, 5f);
        wanderer1.getPosition().set(10f, 5f);
        this.addEntity(wanderer1);

        TestEntity wanderer2 = new TestEntity();
        wanderer2.setShouldWander(false);
        wanderer2.getPosition().set(0, -5f);
        this.addEntity(wanderer2);

        this.emitter1 = new SimpleParticleEmitter(new Vector2f(-5f, -5f), 10, origin -> {
            Vector2f pos = origin.add(MatUtils.randomFloat(-.2f, .2f), MatUtils.randomFloat(-.2f, .2f), new Vector2f());
            Vector2f vel = new Vector2f(MatUtils.randomFloat(-1f, 1f), MatUtils.randomFloat(.5f, 2f));
            return new TestParticle(pos, vel, MatUtils.randomFloat(.7f, 1.5f));
        });

        this.emitter2 = new SimpleParticleEmitter(new Vector2f(5f, -5f), 20, origin -> new TestParticle(origin.add(1f, 1f, new Vector2f()), origin, MatUtils.randomFloat(1f, 2f)).spin());
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

        this.emitter1.tick(delta, something);
        this.emitter2.tick(delta, something);
    }

    @Override
    public void render(AGameOrSomething something, BatchRenderer batch)
    {
        super.render(something, batch);
        this.emitter1.render(something, batch);
        this.emitter2.render(something, batch);
    }
}
