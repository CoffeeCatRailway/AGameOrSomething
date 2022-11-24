package io.github.coffeecatrailway.agameorsomething.common.world;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.BatchRenderer;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.TextureAtlas;
import io.github.coffeecatrailway.agameorsomething.common.entity.PlayerEntity;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import io.github.ocelot.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * @author CoffeeCatRailway
 * Created: 11/11/2022
 */
public abstract class AbstractWorld implements World
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Comparator<Vector2ic> POS_COMPARATOR = (pos1, pos2) -> {
        int r = Integer.compare(pos1.y(), pos2.y()) * -1;
        if (r == 0 && !pos1.equals(pos2))
            r = Integer.compare(pos1.x(), pos2.x());
        return r;
    };

    private static final Vector2i IN_VIEW_POS = new Vector2i();
    private static final Vector2f CORRECT_CAMERA = new Vector2f();

    protected final TreeMap<Vector2ic, Tile> tilesBg; // TODO: Convert to chunk based system
    protected final TreeMap<Vector2ic, Tile> tilesFg;

    public static final int MIN_WORLD_RADIUS = 10;
    protected final int worldRadius; // Distance from 0,0 to each edge
    protected final int worldSize; // Width & height of the world

    private final PlayerEntity player;

    public AbstractWorld(int worldRadius)
    {
        if (worldRadius < MIN_WORLD_RADIUS)
            worldRadius = MIN_WORLD_RADIUS;
        this.worldRadius = worldRadius;
        this.worldSize = this.worldRadius * 2 + 1;

        this.tilesBg = new TreeMap<>(POS_COMPARATOR);
        this.tilesFg = new TreeMap<>(POS_COMPARATOR);

        this.player = new PlayerEntity();
    }

    @Override
    public void tick(float delta, AGameOrSomething something, Camera camera)
    {
        this.player.tick(delta, something, camera, this);

        this.correctCamera(something.getWindow(), camera);
    }

    @Override
    public void render(AGameOrSomething something, BatchRenderer batch, Camera camera)
    {
        Timer.start("tileRendering"); //TODO: Optimize, fix 'lines' appearing, check if background tile is visible
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);

        this.tilesBg.entrySet().stream().filter(entry -> entry.getValue().isVisible() && this.isPosInView(entry.getKey(), something.getWindow(), camera))
                .forEach((entry) -> batch.draw(TextureAtlas.TILE_ATLAS.getEntry(entry.getValue().getObjectId()), entry.getKey().x(), entry.getKey().y(), 1f, 1f));
        this.tilesFg.entrySet().stream().filter(entry -> entry.getValue().isVisible() && this.isPosInView(entry.getKey(), something.getWindow(), camera))
                .forEach((entry) -> batch.draw(TextureAtlas.TILE_ATLAS.getEntry(entry.getValue().getObjectId()), entry.getKey().x(), entry.getKey().y(), 1f, 1f));

        batch.end();
        long millis = Timer.end("tileRendering");
        if (millis >= 30L)
            LOGGER.warn("Tile rendering took {}ms", millis);

        this.player.render(batch, camera);
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

    @Override
    public Tile getTile(Vector2ic pos, boolean foreground)
    {
        Tile tile = foreground ? this.tilesFg.get(pos) : this.tilesBg.get(pos);
        return tile == null ? TileRegistry.AIR.get() : tile;
    }

    @Override
    public Tile setTile(Vector2ic pos, Tile tile, boolean foreground, boolean force)
    {
        if (pos.x() > this.worldRadius || pos.x() < -this.worldRadius || pos.y() > this.worldRadius || pos.y() < -this.worldRadius)
        {
            LOGGER.warn("Tile {} was placed outside of world at position {}", tile.getObjectId(), pos);
            return TileRegistry.AIR.get();
        }
        if (force || !this.getTile(pos, foreground).equals(tile))
        {
            if (foreground)
                return this.tilesFg.put(pos, tile);
            return this.tilesBg.put(pos, tile);
        }
        return TileRegistry.AIR.get();
    }

    public int getWorldRadius()
    {
        return this.worldRadius;
    }

    public int getWorldSize()
    {
        return this.worldSize;
    }
}
