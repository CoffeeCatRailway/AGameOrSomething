package io.github.coffeecatrailway.shipthemagical.world;

//import org.fusesource.jansi.AnsiConsole;

import io.github.coffeecatrailway.shipthemagical.io.Color;
import io.github.coffeecatrailway.shipthemagical.io.Input;
import io.github.coffeecatrailway.shipthemagical.render.Camera;
import io.github.coffeecatrailway.shipthemagical.render.Shader;
import io.github.coffeecatrailway.shipthemagical.world.tiles.TileFarmland;
import io.github.coffeecatrailway.shipthemagical.world.tiles.TileGrass;
import io.github.coffeecatrailway.shipthemagical.world.tiles.TileLeaves;
import io.github.coffeecatrailway.shipthemagical.world.tiles.TileSand;
import io.github.coffeecatrailway.shipthemagical.world.tiles.TileWater;
import io.github.coffeecatrailway.shipthemagical.world.tiles.multi.TileClay;
import io.github.coffeecatrailway.shipthemagical.world.tiles.multi.TileOre;
import io.github.coffeecatrailway.shipthemagical.world.tiles.multi.TilePlanks;
import io.github.coffeecatrailway.shipthemagical.world.tiles.multi.TileStone;
import io.github.coffeecatrailway.shipthemagical.world.tiles.multi.TileStoneBrick;
import io.github.coffeecatrailway.shipthemagical.world.tiles.multi.TileStoneBrickCracked;
import io.github.coffeecatrailway.shipthemagical.world.tiles.multi.TileStoneCracked;
import io.github.coffeecatrailway.shipthemagical.world.tiles.solid.TileBookshelf;
import io.github.coffeecatrailway.shipthemagical.world.tiles.solid.TileBricks;
import io.github.coffeecatrailway.shipthemagical.world.tiles.solid.TileCraftingTable;
import io.github.coffeecatrailway.shipthemagical.world.tiles.solid.TileFurnace;
import io.github.coffeecatrailway.shipthemagical.world.tiles.solid.TileLog;
import io.github.coffeecatrailway.shipthemagical.world.tiles.solid.TilePumpkin;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public abstract class Tile
{

    public static Tile tiles[] = new Tile[255];
    public static int NUMBER_OF_TILES = 0;

    public static final Tile grass = new TileGrass("grass");
    public static final Tile bricks = new TileBricks("bricks");
    public static final Tile water = new TileWater("water");
    public static final Tile sand = new TileSand("sand");
    public static final Tile log = new TileLog("log");
    public static final Tile leaves = new TileLeaves("leaves");

    public static final Tile bookshelf = new TileBookshelf("bookshelf");
    public static final Tile crafting_table = new TileCraftingTable("crafting_table");
    public static final Tile furnace = new TileFurnace("furnace");

    public static final Tile planks_solid = new TilePlanks("planks_solid", true);
    public static final Tile planks = new TilePlanks("planks", false);

    public static final Tile stone_solid = new TileStone("stone_solid", true);
    public static final Tile stone_cracked_solid = new TileStoneCracked("stone_cracked_solid", true);
    public static final Tile stonebrick_solid = new TileStoneBrick("stonebrick_solid", true);
    public static final Tile stonebrick = new TileStoneBrick("stonebrick", false);
    public static final Tile stonebrick_cracked_solid = new TileStoneBrickCracked("stonebrick_cracked_solid", true);
    public static final Tile stonebrick_cracked = new TileStoneBrickCracked("stonebrick_cracked", false);

    public static final Tile farmland = new TileFarmland("farmland");
    public static final Tile pumpkin = new TilePumpkin("pumpkin");

    public static final Tile stone = new TileStone("stone", false);
    public static final Tile stone_cracked = new TileStoneCracked("stone_cracked", false);

    public static final Tile coal_ore = new TileOre("coal_ore", true);
    public static final Tile iron_ore = new TileOre("iron_ore", true);
    public static final Tile gold_ore = new TileOre("gold_ore", true);
    public static final Tile ruby_ore = new TileOre("ruby_ore", true);
    public static final Tile sea_stone_ore = new TileOre("sea_stone_ore", true);
    public static final Tile diamond_ore = new TileOre("diamond_ore", true);
    public static final Tile jaded_envy_ore = new TileOre("jaded_envy_ore", true);

    public static final Tile clay = new TileClay("clay", false);
    public static final Tile hardened_clay = new TileClay("hardened_clay", true);

    private byte id;
    private boolean solid;
    private String texture;

    /**
     * Tile.java constructor.
     *
     * @param texture {@code String}
     */
    public Tile(String texture)
    {
        this.id = (byte) NUMBER_OF_TILES;
        NUMBER_OF_TILES++;
//        AnsiConsole.out.println("Tile: [" + Color.toColor(Color.GREEN) + texture + Color.toColor(Color.DEFAULT)
//                + "] with id of: [" + Color.toColor(Color.GREEN) + id + Color.toColor(Color.DEFAULT) + "] loaded!");
        this.texture = texture;
        this.solid = false;

        if (tiles[id] != null)
        {
            throw new IllegalStateException("Tiles at: [" + id + "] is already being used!");
        }
        tiles[id] = this;
    }

    /**
     * Updates tile.
     *
     * @param input  {@code Input}
     * @param shader {@code Shader}
     * @param world  {@code Matrix4f}
     * @param cam    {@code Camera}
     */
    public abstract void update(Input input, Shader shader, World world, Camera cam);

    /**
     * Set tile solid.
     *
     * @param solid {@code Boolean}
     * @return {@code Tile}
     */
    public Tile setSolid(boolean solid)
    {
        this.solid = solid;
        return this;
    }

    /**
     * Check id tile is solid.
     *
     * @return {@code Boolean}
     */
    public boolean isSolid()
    {
        return solid;
    }

    /**
     * Set tile id.
     *
     * @param id {@code Byte}
     */
    public void setId(byte id)
    {
        this.id = id;
    }

    /**
     * Get tile id.
     *
     * @return {@code Byte}
     */
    public byte getId()
    {
        return id;
    }

    /**
     * Sets tile texture.
     *
     * @param texture {@code String}
     */
    public void setTexture(String texture)
    {
        this.texture = texture;
    }

    /**
     * Get tile texture.
     *
     * @return {@code String}
     */
    public String getTexture()
    {
        return texture;
    }
}
