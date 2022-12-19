package io.github.coffeecatrailway.agameorsomething.common.world;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.BatchRenderer;
import io.github.coffeecatrailway.agameorsomething.client.render.LineRenderer;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.atlas.TextureAtlas;
import io.github.coffeecatrailway.agameorsomething.common.collision.BoundingBox;
import io.github.coffeecatrailway.agameorsomething.common.entity.Entity;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
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

    private final Map<Vector2ic, Tile> tilesBg = new HashMap<>(); // TODO: Convert to chunk based system
    private final Map<Vector2ic, Tile> tilesFg = new HashMap<>();
    private final Map<Vector2ic, BoundingBox> boundingBoxes = new HashMap<>();
    private final Set<Entity> entities = new HashSet<>();

    public static final int MIN_WORLD_RADIUS = 10;
    protected final int worldRadius; // Distance from 0,0 to each edge
    protected final int worldSize; // Width & height of the world

    public AbstractWorld(int worldRadius)
    {
        if (worldRadius < MIN_WORLD_RADIUS)
            worldRadius = MIN_WORLD_RADIUS;
        this.worldRadius = worldRadius;
        this.worldSize = this.worldRadius * 2 + 1;
    }

    @Override
    public void tick(float delta, AGameOrSomething something)
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
    public void render(AGameOrSomething something, BatchRenderer batch)
    {
        Timer.start("tileRendering"); //TODO: Optimize, check if background tile is visible
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);

        // Render tiles
        this.getViewableTiles(something, false).forEach((entry) -> batch.draw(TextureAtlas.TILE_ATLAS.getEntry(entry.getValue().getObjectId()), entry.getKey().x(), entry.getKey().y(), entry.getValue().getBounds().x(), entry.getValue().getBounds().y()));
        this.getViewableTiles(something, true).forEach((entry) -> batch.draw(TextureAtlas.TILE_ATLAS.getEntry(entry.getValue().getObjectId()), entry.getKey().x(), entry.getKey().y(), entry.getValue().getBounds().x(), entry.getValue().getBounds().y()));

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
        }).forEach(entity -> {
            entity.render(something, batch);
            if (AGameOrSomething.isDebugRender()) // Render entity bounds
            {
                LineRenderer.setLineColor(1f, 0f, 0f);
                LineRenderer.drawBox(entity.getPosition(), entity.getBounds().add(entity.getPosition(), new Vector2f()));
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
        camera.getPosition().get(CORRECT_CAMERA);
//        float borderX = this.worldRadius - ((window.getWidth() / (float) this.worldRadius) / 2f) / camera.getZoom();
        float borderX = this.worldRadius - 4f;
        float borderY = this.worldRadius - 4f;

//        System.out.println("Camera Position (" + CORRECT_CAMERA.x + ", " + CORRECT_CAMERA.y + ") - Border X/Y (" + borderX + ", " + borderY + ")");
        if (CORRECT_CAMERA.x > borderX)
            CORRECT_CAMERA.x = borderX;
        if (CORRECT_CAMERA.x < -borderX)
            CORRECT_CAMERA.x = -borderX;
        if (CORRECT_CAMERA.y > borderY)
            CORRECT_CAMERA.y = borderY;
        if (CORRECT_CAMERA.y < -borderY)
            CORRECT_CAMERA.y = -borderY;
        camera.setPosition(CORRECT_CAMERA);
    }

    /**
     * @param pos    {@link Vector2ic} - Tile based position
     * @param window {@link Window} - Application window
     * @param camera {@link Camera} - Main game camera
     * @return True if intersect is within view on screen
     */
    public boolean isPosInView(Vector2ic pos, Window window, Camera camera)
    {
        int viewWidth = (int) (window.getFramebufferWidth() / 4f / camera.getZoom()) + 5;
        int viewHeight = (int) (window.getFramebufferHeight() / 4f / camera.getZoom()) + 5;
        pos.add((int) -camera.getPosition().x(), (int) -camera.getPosition().y(), IN_VIEW_POS);
        return IN_VIEW_POS.x() > -viewWidth && IN_VIEW_POS.x() < viewWidth && IN_VIEW_POS.y() > -viewHeight && IN_VIEW_POS.y() < viewHeight;
//        Vector4f p = new Vector4f(1.0F).mul(camera.getProjectionMatrix()).mul(camera.getViewMatrix()).mul(pos.x(), pos.y(), 0.0F, 1.0F);
//        return p.lengthSquared() < 1;
    }

    public Stream<Map.Entry<Vector2ic, Tile>> getViewableTiles(AGameOrSomething something, boolean foreground)
    {
        if (foreground)
            return this.tilesFg.entrySet().stream().filter(entry -> entry.getValue().isVisible() && this.isPosInView(entry.getKey(), something.getWindow(), something.getCamera())).sorted(POS_COMPARATOR);
        return this.tilesBg.entrySet().stream().filter(entry -> entry.getValue().isVisible() && this.isPosInView(entry.getKey(), something.getWindow(), something.getCamera())).sorted(POS_COMPARATOR);
    }

    @Override
    public Tile getTile(Vector2ic pos, boolean foreground)
    {
        Tile tile = foreground ? this.tilesFg.get(pos) : this.tilesBg.get(pos);
        return tile == null ? TileRegistry.AIR.get() : tile;
    }

    @Override
    public BoundingBox getTileBounds(Vector2ic pos)
    {
        return this.boundingBoxes.get(pos);
    }

    @Override
    public Tile setTile(Vector2ic pos, Tile tile, boolean foreground)
    {
        if (pos.x() > this.worldRadius || pos.x() < -this.worldRadius || pos.y() > this.worldRadius || pos.y() < -this.worldRadius)
        {
            LOGGER.warn("Tile {} was placed outside of world at position {}", tile.getObjectId(), pos);
            return TileRegistry.AIR.get();
        }
        if (!this.getTile(pos, foreground).equals(tile))
        {
            if (foreground)
            {
                if (tile.isCollidable())
                    this.boundingBoxes.put(pos, new BoundingBox(new Vector2f(pos.x(), pos.y()), tile.getBounds()));
                else
                    this.boundingBoxes.put(pos, BoundingBox.EMPTY);
                return this.tilesFg.put(pos, tile);
            }
            return this.tilesBg.put(pos, tile);
        }
        return TileRegistry.AIR.get();
    }

    @Override
    public void addEntity(Entity entity)
    {
        if (this.entities.stream().map(Entity::getUUID).anyMatch(uuid -> uuid.equals(entity.getUUID())))
            throw new IllegalStateException("Entity with uuid '" + entity.getUUID().toString() + "' already exists!");
        this.entities.add(entity);
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
}
