package io.github.coffeecatrailway.agameorsomething.core.registry;

import io.github.coffeecatrailway.agameorsomething.client.render.Texture;
import io.github.coffeecatrailway.agameorsomething.client.render.vbo.VBOModels;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public class TileRegistry
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final SomethingRegistry<Tile> TILES = SomethingRegistry.create(TileRegistry.AIR);

    public static final Supplier<Tile> AIR = TILES.register("air", () -> new Tile(new Tile.TileData().setHasTexture(false).build()));
    public static final Supplier<Tile> GRASS = TILES.register("grass", () -> new Tile(new Tile.TileData().setHarvestLevel(1).setDrop(null).build()));
    public static final Supplier<Tile> DIRT = TILES.register("dirt", () -> new Tile(new Tile.TileData().setHarvestLevel(1).setDrop(null).build()));
    public static final Supplier<Tile> SAND = TILES.register("sand", () -> new Tile(new Tile.TileData().setHarvestLevel(1).setDrop(null).build()));
    public static final Supplier<Tile> TEST = TILES.register("test", () -> new Tile(new Tile.TileData().setHarvestLevel(100).setCustomTexture(Texture.MISSING).setModel(VBOModels.SIMPLE_1X2).build()));

    public static void load()
    {
        LOGGER.info("Tiles registry loaded");
    }
}
