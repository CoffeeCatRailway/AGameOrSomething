package io.github.coffeecatrailway.agameorsomething.core.registry;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import org.joml.Vector2f;
import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public class TileRegistry
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final SomethingRegistry<Tile> TILES = SomethingRegistry.create(Tile.class);

    public static final Supplier<Tile> AIR = TILES.register("air", () -> new Tile(new Tile.TileData().setHasTexture(false).setBounds(new Vector2f(0f)).build()));
    public static final Supplier<Tile> GRASS = TILES.register("grass", () -> new Tile(new Tile.TileData().setHarvestLevel(1).setDrop(null).build()));
    public static final Supplier<Tile> DIRT = TILES.register("dirt", () -> new Tile(new Tile.TileData().setHarvestLevel(1).setDrop(null).build()));
    public static final Supplier<Tile> SAND = TILES.register("sand", () -> new Tile(new Tile.TileData().setHarvestLevel(1).setDrop(null).build()));
//    public static final Supplier<Tile> TEST = TILES.register("test", () -> new Tile(new Tile.TileData().setHarvestLevel(100).setCustomTexture(new ObjectLocation("missing.png")).setModel(VBOModels.SIMPLE_1X2).build()));

    public static void load()
    {
        LOGGER.info("Tile registry loaded");
    }
}
