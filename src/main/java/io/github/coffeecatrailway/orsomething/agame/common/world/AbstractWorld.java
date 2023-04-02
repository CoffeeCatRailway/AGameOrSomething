package io.github.coffeecatrailway.orsomething.agame.common.world;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.orsomething.agame.client.texture.atlas.Atlases;
import io.github.coffeecatrailway.orsomething.agame.core.AGameOrSomething;
import io.github.coffeecatrailway.orsomething.agame.core.registry.AGameTiles;
import io.github.coffeecatrailway.orsomething.anengine.client.BatchRenderer;
import io.github.coffeecatrailway.orsomething.anengine.client.LineRenderer;
import io.github.coffeecatrailway.orsomething.anengine.client.camera.Camera;
import io.github.coffeecatrailway.orsomething.anengine.common.Timer;
import io.github.coffeecatrailway.orsomething.anengine.common.entity.Entity;
import io.github.coffeecatrailway.orsomething.anengine.common.tile.Tile;
import io.github.coffeecatrailway.orsomething.anengine.common.world.TileSet;
import io.github.coffeecatrailway.orsomething.anengine.common.world.World;
import io.github.coffeecatrailway.orsomething.anengine.core.AnEngineOrSomething;
import io.github.ocelot.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author CoffeeCatRailway
 * Created: 11/11/2022
 */
public abstract class AbstractWorld implements World
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Comparator<Map.Entry<Vector2ic, Tile>> POS_COMPARATOR = (entry1, entry2) -> {
        int r = Integer.compare(entry1.getKey().y(), entry2.getKey().y()) * -1;
        if (r == 0 && !entry1.getKey().equals(entry2.getKey()))
            r = Integer.compare(entry1.getKey().x(), entry2.getKey().x());
        return r;
    };

    private static final Vector2i IN_VIEW_POS = new Vector2i();
    private static final Vector2f CORRECT_CAMERA = new Vector2f();

    private final TileSet background = new TileSet();
    //    private final TileSet midground = new TileSet();
    private final TileSet foreground = new TileSet();
    private final Set<Entity> entities = new HashSet<>();

    public static final int MIN_WORLD_RADIUS = 10;
    protected final int worldRadius; // Distance from 0,0 to each edge
    protected final int worldSize; // Width & height of the world

    protected final Random random;

    public AbstractWorld(int worldRadius)
    {
        if (worldRadius < MIN_WORLD_RADIUS)
            worldRadius = MIN_WORLD_RADIUS;
        this.worldRadius = worldRadius;
        this.worldSize = this.worldRadius * 2 + 1;

        this.random = new Random();
    }

    @Override
    public void tick(float delta, AnEngineOrSomething something)
    {
        for (Entity entity : this.entities)
        {
            entity.tick(delta, something, something.getCamera(), this);

            entity.checkTileCollision(this);
            this.getEntitiesWithin(entity.getPosition(), 3f).forEach(entity::checkEntityCollision);
            entity.checkTileCollision(this);
        }

        this.correctCamera(something.getWindow(), something.getCamera());
    }

    @Override
    public void render(AnEngineOrSomething something, BatchRenderer batch)
    {
        Timer.start("tileRendering"); //TODO: Optimize, check if background tile is visible
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);

        // Render tiles
        this.getViewableTiles(something.getCamera(), TileSet.Level.BACKGROUND).forEach((entry) -> batch.draw(Atlases.TILE_ATLAS.getEntry(entry.getValue().getObjectId()), entry.getKey().x(), entry.getKey().y(), entry.getValue().getBounds().x(), entry.getValue().getBounds().y()));
//        this.getViewableTiles(something.getCamera(), TileSet.Level.MIDGROUND).forEach((entry) -> batch.draw(TextureAtlas.TILE_ATLAS.getEntry(entry.getValue().getObjectId()), entry.getKey().x(), entry.getKey().y(), entry.getValue().getBounds().x(), entry.getValue().getBounds().y()));
        this.getViewableTiles(something.getCamera(), TileSet.Level.FOREGROUND).forEach((entry) -> batch.draw(Atlases.TILE_ATLAS.getEntry(entry.getValue().getObjectId()), entry.getKey().x(), entry.getKey().y(), entry.getValue().getBounds().x(), entry.getValue().getBounds().y()));

        batch.end();
        long millis = Timer.end("tileRendering");
        if (millis >= 30L)
            LOGGER.warn("Tile rendering took {}ms", millis);

        // Render entities
        this.entities.stream().sorted((entity1, entity2) -> {
            int r = Float.compare(entity1.getPosition().y(), entity2.getPosition().y()) * -1;
            if (r == 0 && !entity1.equals(entity2))
                r = Float.compare(entity1.getPosition().x(), entity2.getPosition().x());
            return r;
        }).filter(entity -> something.getCamera().getCullingFilter().isInside(entity.getBoundingBox())).forEach(entity -> {
            entity.render(something, batch);
            if (AGameOrSomething.DEBUG_RENDER.get()) // Render entity bounds
            {
                LineRenderer.INSTANCE.begin(1f, 0f, 0f);
                LineRenderer.INSTANCE.drawBox(entity.getBoundingBox());
                LineRenderer.INSTANCE.end();
            }
        });
    }

    /**
     * Corrects camera position to stay within the world
     *
     * @param window {@link Window} - Application window
     * @param camera {@link Camera} - Main game camera
     */
    public void correctCamera(Window window, Camera camera)
    {
        CORRECT_CAMERA.set(camera.getPosition());

        float bx = this.worldRadius;
        float by = this.worldRadius;

        if (CORRECT_CAMERA.x > bx)
            CORRECT_CAMERA.x = bx;
        if (CORRECT_CAMERA.x < -bx)
            CORRECT_CAMERA.x = -bx;
        if (CORRECT_CAMERA.y > by)
            CORRECT_CAMERA.y = by;
        if (CORRECT_CAMERA.y < -by)
            CORRECT_CAMERA.y = -by;

        camera.setPosition(CORRECT_CAMERA);
    }

    public Stream<Map.Entry<Vector2ic, Tile>> getViewableTiles(Camera camera, TileSet.Level level)
    {
        return this.getTileSet(level).entryStream().filter(entry -> entry.getValue().isVisible() && camera.getCullingFilter().isInside(this.getTileBounds(entry.getKey(), level))).sorted(POS_COMPARATOR);
    }

    @Override
    public TileSet getTileSet(TileSet.Level level)
    {
        return switch (level)
                {
                    case BACKGROUND -> this.background;
                    case MIDGROUND -> this.foreground;
                    case FOREGROUND -> this.foreground;
                };
    }

    @Override
    public Tile setTile(Vector2ic pos, Tile tile, TileSet.Level level)
    {
        if (pos.x() > this.worldRadius || pos.x() < -this.worldRadius || pos.y() > this.worldRadius || pos.y() < -this.worldRadius)
        {
            LOGGER.warn("Tile {} was placed outside of world at position {}", tile.getObjectId(), pos);
            return AGameTiles.AIR.get();
        }
        return World.super.setTile(pos, tile, level);
    }

    @Override
    public void addEntity(Entity entity)
    {
        if (this.entities.stream().map(Entity::getUUID).anyMatch(uuid -> uuid.equals(entity.getUUID())))
            throw new IllegalStateException("Entity with uuid '" + entity.getUUID().toString() + "' already exists!");
        LOGGER.debug("Entity '" + entity.getObjectId() + "' with uuid '" + entity.getUUID().toString() + "' added to world");
        entity.setWorld(this);
        entity.init();
        this.entities.add(entity);
    }

    @Override
    public Entity getEntityByUUID(UUID uuid)
    {
        return this.entities.stream().filter(entity -> entity.getUUID().equals(uuid)).findFirst().orElseThrow(() -> new IllegalStateException("Entity with uuid '" + uuid.toString() + "' doesn't exists!"));
    }

    public Set<Entity> getEntitiesWithin(Vector2fc origin, float radius)
    {
        return this.entities.stream().filter(entity -> entity.getPosition().distance(origin) <= radius).collect(Collectors.toSet());
    }

    @Override
    public int getWorldRadius()
    {
        return this.worldRadius;
    }

    @Override
    public int getWorldSize()
    {
        return this.worldSize;
    }

    @Override
    public Random random()
    {
        return this.random;
    }
}
