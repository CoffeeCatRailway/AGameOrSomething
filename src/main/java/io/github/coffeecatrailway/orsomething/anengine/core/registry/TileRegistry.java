package io.github.coffeecatrailway.orsomething.anengine.core.registry;

import io.github.coffeecatrailway.orsomething.anengine.common.tile.AirTile;
import io.github.coffeecatrailway.orsomething.anengine.common.tile.Tile;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public abstract class TileRegistry implements SomethingRegistry<Tile>
{
    public static final SomethingRegistry.Registry<Tile> REGISTRY = SomethingRegistry.Registry.create(Tile.class);

    public static final Supplier<AirTile> AIR = REGISTRY.register("air", AirTile::new);

    @Override
    public @Nullable Supplier<? extends Tile> getDefault()
    {
        return AIR;
    }
}
