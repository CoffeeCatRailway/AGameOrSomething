package io.github.coffeecatrailway.orsomething.anengine.common.tile;

import io.github.coffeecatrailway.orsomething.anengine.common.tile.Tile;
import org.joml.Vector2i;

/**
 * @author CoffeeCatRailway
 * Created: 20/12/2022
 */
public class AirTile extends Tile
{
    public AirTile()
    {
        super(new Tile.TileData().setHasTexture(false).setBounds(new Vector2i(0)).build());
    }

    @Override
    public boolean isVisible()
    {
        return false;
    }

    @Override
    public boolean isCollidable()
    {
        return false;
    }

    @Override
    public boolean isReplaceable()
    {
        return true;
    }
}
