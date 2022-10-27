package io.github.coffeecatrailway.shipthemagical.world;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

//import org.fusesource.jansi.AnsiConsole;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import io.github.coffeecatrailway.shipthemagical.collision.AABB;
import io.github.coffeecatrailway.shipthemagical.entity.Entity;
import io.github.coffeecatrailway.shipthemagical.entity.Transform;
import io.github.coffeecatrailway.shipthemagical.entity.entities.EntityCoin;
import io.github.coffeecatrailway.shipthemagical.entity.entities.EntityPlayer;
import io.github.coffeecatrailway.shipthemagical.entity.entities.EntityTest;
import io.github.coffeecatrailway.shipthemagical.entity.entities.EntityTest.TestEntityType;
import io.github.coffeecatrailway.shipthemagical.entity.entities.EntityCoin.CoinType;
import io.github.coffeecatrailway.shipthemagical.entity.entities.EntityElder;
import io.github.coffeecatrailway.shipthemagical.entity.entities.EntityElder.ElderType;
import io.github.coffeecatrailway.shipthemagical.io.Color;
import io.github.coffeecatrailway.shipthemagical.io.Config;
import io.github.coffeecatrailway.shipthemagical.io.Config.PropertyType;
import io.github.coffeecatrailway.shipthemagical.io.Window;
import io.github.coffeecatrailway.shipthemagical.render.Camera;
import io.github.coffeecatrailway.shipthemagical.render.Shader;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class World
{

    private byte[] tiles;
    private AABB[] bounding_boxes;
    // private List<Entity> entities;
    private Entity[] entities;

    private int entity_index;

    private int width;
    private int height;
    private int scale;

    private int viewX;
    private int viewY;

    private String level = "./res/levels/";

    private Matrix4f world;
    private static Config config = new Config("game.config", Config.RES);

    public EntityCoin coin_gold;
    public EntityCoin coin_bronze;
    public EntityCoin coin_silver;

    public EntityPlayer player;
    public EntityTest test_p;
    public EntityTest test_t;

    public EntityElder elder_red;
    public EntityElder elder_blue;
    public EntityElder elder_green;
    public EntityElder elder_yellow;

    /**
     * World.java constructor. Sets world to .lvl world.
     *
     * @param level  {@code String}
     * @param camera {@code Camera}
     */
    public World(String level, Camera camera)
    {
        this.level += level;
        scale = (int) config.getProperty("game.world.scale", PropertyType.INT);
        entities = new Entity[100];

        world = new Matrix4f().setTranslation(new Vector3f(0));
        world.scale(scale);

        width = getLvlFile(this.level + "/entities.lvl")[0].length;
        height = getLvlFile(this.level + "/entities.lvl").length;

        tiles = new byte[width * height];
        bounding_boxes = new AABB[width * height];
        Config properties = new Config(level + "/level.properties", Config.LEVELS);
        System.out.println("World: [" + this.level + "] loaded!");

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int tile_index = getLvlFile(this.level + "/tiles.lvl")[y][x];
                int entity_lvl = getLvlFile(this.level + "/entities.lvl")[y][x];

                if (tile_index == 0)
                {
                    tile_index = (int) properties.getProperty("level.main_tile", PropertyType.INT);
                }

                Tile t = Tile.tiles[tile_index];
                if (t != null)
                {
                    setTile(t, x, y);
                }
                setEntities(x, y, entity_lvl, entity_lvl, camera);
            }
        }
    }

    /**
     * Sets entities in world.
     *
     * @param x      {@code Integer}
     * @param y      {@code Integer}
     * @param entity {@code Integer}
     * @param alpha  {@code Integer}
     * @param camera {@code Camera}
     */
    private void setEntities(int x, int y, int entity, int alpha, Camera camera)
    {
        Transform transform;

        if (alpha > 0)
        {
            transform = new Transform();
            transform.pos.x = x * 2;
            transform.pos.y = -y * 2;

            switch (entity)
            {
                // Coins
                case 1:
                    coin_gold = new EntityCoin(transform, false, CoinType.GOLD);
                    entities[entity_index] = coin_gold;
                    break;
                case 2:
                    coin_bronze = new EntityCoin(transform, false, CoinType.COPPER);
                    entities[entity_index] = coin_bronze;
                    break;
                case 3:
                    coin_silver = new EntityCoin(transform, false, CoinType.SILVER);
                    entities[entity_index] = coin_silver;
                    break;
                // Player & Test Entity
                case 4:
                    player = new EntityPlayer(transform, true);
                    entities[entity_index] = player;
                    camera.getPosition().set(transform.pos.mul(-scale, new Vector3f()));
                    break;
                case 5:
                    test_p = new EntityTest(transform, true, TestEntityType.FOLLOW_P);
                    entities[entity_index] = test_p;
                    break;
                case 6:
                    test_t = new EntityTest(transform, true, TestEntityType.FOLLOW_T);
                    entities[entity_index] = test_t;
                    break;
                // Elders
                case 7:
                    elder_red = new EntityElder(transform, false, ElderType.FIRE);
                    entities[entity_index] = elder_red;
                    break;
                case 8:
                    elder_blue = new EntityElder(transform, false, ElderType.WATER);
                    entities[entity_index] = elder_blue;
                    break;
                case 9:
                    elder_green = new EntityElder(transform, false, ElderType.EARTH);
                    entities[entity_index] = elder_green;
                    break;
                case 10:
                    elder_yellow = new EntityElder(transform, false, ElderType.AIR);
                    entities[entity_index] = elder_yellow;
                    break;
                default:
                    break;
            }
            if (entities[entity_index] != null)
//				AnsiConsole.out.println("Entity: [" + Color.toColor(Color.GREEN) + entities[entity_index]
//						+ Color.toColor(Color.DEFAULT) + "] loaded at position: [" + x + "," + y + "]!");
                entity_index++;
        }
    }

    /**
     * Gets the map/level from .lvl file
     *
     * @param filename {@code String}
     * @return map {@code Integer[][]}
     */
    private int[][] getLvlFile(String filename)
    {
        int[][] map = null;
        try
        {
            Scanner scn = new Scanner(new BufferedReader(new FileReader(filename)));

            String line = scn.nextLine();
            String split = "\\s+";

            String[] numbers = line.split(split);
            int lines = 1;

            while (scn.hasNextLine())
            {
                lines++;
                scn.nextLine();
            }
            scn.close();

            scn = new Scanner(new BufferedReader(new FileReader(filename)));
            map = new int[lines][numbers.length];

            for (int y = 0; y < lines; y++)
            {
                for (int x = 0; x < numbers.length; x++)

                    map[y][x] = Integer.valueOf(numbers[x]);

                line = scn.nextLine();
                numbers = line.split(split);
            }

            Collections.rotate(Arrays.asList(map), -1);
            scn.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Gets world width.
     *
     * @return {@code Integer}
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Gets world height.
     *
     * @return {@code Integer}
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Gets world scale.
     *
     * @return {@code Integer}
     */
    public int getScale()
    {
        return scale;
    }

    /**
     * Get world matrix.
     *
     * @return {@code Matrix4f}
     */
    public Matrix4f getWorldMatrix()
    {
        return world;
    }

    /**
     * Calculate the view.
     *
     * @param window {@code Window}
     */
    public void calcView(Window window)
    {
        viewX = (window.getWidth() / (scale * 2)) + 4;
        viewY = (window.getHeight() / (scale * 2)) + 4;
    }

    /**
     * Renders the world.
     *
     * @param render {@code TileRenderer}
     * @param shader {@code Shader}
     * @param camera {@code Camera}
     */
    public void render(TileRenderer render, Shader shader, Camera camera)
    {
        int posX = (int) camera.getPosition().x / (scale * 2);
        int posY = (int) camera.getPosition().y / (scale * 2);

        for (int i = 0; i < viewX; i++)
        {
            for (int j = 0; j < viewY; j++)
            {
                Tile tile = getTile(i - posX - (viewX / 2) + 1, j + posY - (viewY / 2) + 1);
                if (tile != null)
                {
                    render.renderTile(tile, i - posX - (viewX / 2) + 1, -j - posY + (viewY / 2) - 1, shader, world,
                            camera);
                }
            }
        }

        for (int i = 0; i < entities.length; i++)
        {
            if (entities[i] != null)
                entities[i].render(shader, camera, this);
        }
    }

    /**
     * Updates the world.
     *
     * @param delta  {@code Float}
     * @param window {@code Window}
     * @param shader {@code Shader}
     * @param camera {@code Camera}
     */
    public void update(float delta, Window window, Shader shader, Camera camera)
    {
        int posX = (int) camera.getPosition().x / (scale * 2);
        int posY = (int) camera.getPosition().y / (scale * 2);

        for (int i = 0; i < viewX; i++)
        {
            for (int j = 0; j < viewY; j++)
            {
                int tileX = i - posX - (viewX / 2) + 1;
                int tileY = j + posY - (viewY / 2) + 1;
                Tile tile = getTile(tileX, tileY);
                if (tile != null)
                {
                    tile.update(window.getInput(), shader, this, camera);
                }
            }
        }

        for (int i = 0; i < entities.length; i++)
        {
            if (entities[i] != null)
                entities[i].update(delta, window, camera, this);
        }

        for (int i = 0; i < entities.length; i++)
        {
            if (entities[i] != null)
            {
                entities[i].collideWithTiles(this);
                for (int j = i + 1; j < entities.length; j++)
                {
                    if (entities[j] != null)
                        if (entities[i].hasEntityCollision())
                            entities[i].collideWithEntity(entities[j]);
                }
                entities[i].collideWithTiles(this);
            }
        }
    }

    /**
     * Corrects world camera.
     *
     * @param cam    {@code Camera}
     * @param window {@code Window}
     */
    public void correctCamera(float delta, Camera cam, Window window)
    {
        Vector3f pos = cam.getPosition();
        // pos.add(new Vector3f(0, 0, 0.5f * delta));

        int w = -width * scale * 2;
        int h = height * scale * 2;

        if (pos.x > -(window.getWidth() / 2) + scale)
            pos.x = -(window.getWidth() / 2) + scale;
        if (pos.x < w + (window.getWidth() / 2) + scale)
            pos.x = w + (window.getWidth() / 2) + scale;

        if (pos.y < (window.getHeight() / 2) - scale)
            pos.y = (window.getHeight() / 2) - scale;
        if (pos.y > h - (window.getHeight() / 2) - scale)
            pos.y = h - (window.getHeight() / 2) - scale;

    }

    /**
     * Set tile at x, y.
     *
     * @param tile {@code Tile}
     * @param x    {@code Integer}
     * @param y    {@code Integer}
     */
    public void setTile(Tile tile, int x, int y)
    {
        tiles[x + y * width] = tile.getId();
        if (tile.isSolid())
        {
            bounding_boxes[x + y * width] = new AABB(new Vector2f(x * 2, -y * 2), new Vector2f(1, 1));
        } else
        {
            bounding_boxes[x + y * width] = null;
        }
    }

    /**
     * Get tile at x, y.
     *
     * @param x {@code Integer}
     * @param y {@code Integer}
     * @return {@code Tile}
     */
    public Tile getTile(int x, int y)
    {
        try
        {
            return Tile.tiles[tiles[x + y * width]];
        } catch (ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
    }

    /**
     * Get tile bounding boxes.
     *
     * @param x {@code Integer}
     * @param y {@code Integer}
     * @return {@code AABB}
     */
    public AABB getTileBoundingBox(int x, int y)
    {
        try
        {
            return bounding_boxes[x + y * width];
        } catch (ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
    }
}
