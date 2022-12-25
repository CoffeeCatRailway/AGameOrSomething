package io.github.coffeecatrailway.agameorsomething.common.world;

import io.github.coffeecatrailway.agameorsomething.client.render.BatchRenderer;
import io.github.coffeecatrailway.agameorsomething.common.collision.BoundingBox;
import io.github.coffeecatrailway.agameorsomething.common.entity.Entity;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.common.utils.TilePos;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import org.joml.*;

import java.lang.Math;

/**
 * @author CoffeeCatRailway
 * Created: 12/11/2022
 */
public interface World
{
    void generate();

    void tick(float delta, AGameOrSomething something);

    void render(AGameOrSomething something, BatchRenderer batch);

    TileSet getTileSet(TileSet.Level level);

    default Tile getTile(TilePos pos)
    {
        return this.getTile(pos.pos(), pos.level());
    }

    Tile getTile(Vector2ic pos, TileSet.Level level);

    BoundingBox getTileBounds(Vector2ic pos, TileSet.Level level);

    Tile setTile(Vector2ic pos, Tile tile, TileSet.Level level);

    /**
     * @return If tile can be placed at position
     */
    default boolean canPlaceTileAt(Vector2ic pos, Tile tile, TileSet.Level level)
    {
        Vector2i tmp = new Vector2i();
        if (tile.getBounds().x() < 0 || tile.getBounds().y() < 0)
            return false;
        for (int y = 0; y < tile.getBounds().y() - 1; y++)
            for (int x = 0; x < tile.getBounds().x() - 1; x++)
                if (!this.getTile(pos.add(x, y, tmp), level).isReplaceable())
                    return false;
        return this.getTile(pos, level).isReplaceable();
    }

    default TilePos trace(Vector3fc start, Vector3fc end)
    {
        Vector3f ray = end.sub(start, new Vector3f()).normalize();

        float t = -start.z() / ray.z();

        float dx = start.x() + ray.x() * t;
        float dy = start.y() + ray.y() * t;

        if (t < 0 || t >= end.length())
            return TilePos.EMPTY;

        Vector2i pos = new Vector2i((int) Math.floor(dx), (int) Math.floor(dy));
        Tile tile = this.getTile(pos, TileSet.Level.FOREGROUND); // Probably a better way of doing this...
        if (tile == TileRegistry.AIR.get())
        {
            tile = this.getTile(pos, TileSet.Level.MIDGROUND);
            if (tile == TileRegistry.AIR.get())
            {
                tile = this.getTile(pos, TileSet.Level.BACKGROUND);
                if (tile == TileRegistry.AIR.get())
                    return TilePos.EMPTY;
                return new TilePos(new Vector2f(dx, dy), pos, TileSet.Level.BACKGROUND);
            }
            return new TilePos(new Vector2f(dx, dy), pos, TileSet.Level.MIDGROUND);
        }
        return new TilePos(new Vector2f(dx, dy), pos, TileSet.Level.FOREGROUND);
    }

    void addEntity(Entity entity);

    int getWorldRadius();

    int getWorldSize();
}
