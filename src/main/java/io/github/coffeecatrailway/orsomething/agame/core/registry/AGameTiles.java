package io.github.coffeecatrailway.orsomething.agame.core.registry;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.orsomething.anengine.common.tile.Tile;
import io.github.coffeecatrailway.orsomething.anengine.core.registry.TileRegistry;
import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * @author CoffeeCatRailway
 * Created: 02/04/2023
 */
public class AGameTiles extends TileRegistry
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final AGameTiles TILES = new AGameTiles();

    public static final Supplier<Tile> GRASS = AGameTiles.REGISTRY.register("grass", () -> new Tile(new Tile.TileData().setHarvestLevel(1).setDrop(null).build()));
    public static final Supplier<Tile> DIRT = AGameTiles.REGISTRY.register("dirt", () -> new Tile(new Tile.TileData().setHarvestLevel(1).setDrop(null).build()));
    public static final Supplier<Tile> SAND = AGameTiles.REGISTRY.register("sand", () -> new Tile(new Tile.TileData().setHarvestLevel(1).setDrop(null).build()));
//    public static final Supplier<Tile> TEST = TILES.register("test", () -> new Tile(new Tile.TileData().setHarvestLevel(100).setCustomTexture(new ObjectLocation("missing.png")).setModel(VBOModels.SIMPLE_1X2).build()));

    @Override
    public void load()
    {
        LOGGER.info("Tile registry loaded");
    }
}
