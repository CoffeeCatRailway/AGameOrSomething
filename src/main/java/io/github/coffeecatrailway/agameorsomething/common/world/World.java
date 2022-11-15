package io.github.coffeecatrailway.agameorsomething.common.world;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
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

    void tick(AGameOrSomething something, Camera camera);

    void render(AGameOrSomething something, Camera camera);

    default Tile getTile(TilePos pos)
    {
        return this.getTile(pos.pos(), pos.foreground());
    }

    Tile getTile(Vector2ic pos, boolean foreground);

    default Tile setTile(Vector2ic pos, Tile tile, boolean foreground)
    {
        return this.setTile(pos, tile, foreground, false);
    }

    Tile setTile(Vector2ic pos, Tile tile, boolean foreground, boolean force);

    default TilePos trace(Vector3fc start, Vector3fc end)
    {
        Vector3f ray = end.sub(start, new Vector3f()).normalize();

        float t = -start.z() / ray.z();

        float dx = start.x() + ray.x() * t;
        float dy = start.y() + ray.y() * t;

        if (t < 0 || t >= end.length())
            return TilePos.EMPTY;

        Vector2i pos = new Vector2i((int) Math.floor(dx), (int) Math.floor(dy));
        Tile tile = this.getTile(pos, false);
        if (tile == TileRegistry.AIR.get())
        {
            tile = this.getTile(pos, true);
            if (tile == TileRegistry.AIR.get())
                return TilePos.EMPTY;
            return new TilePos(new Vector2f(dx, dy), pos, true);
        }
        return new TilePos(new Vector2f(dx, dy), pos, false);
    }
}
