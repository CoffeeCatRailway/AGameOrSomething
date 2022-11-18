package io.github.coffeecatrailway.agameorsomething.client.render.texture;

import com.google.gson.*;
import io.github.coffeecatrailway.agameorsomething.common.entity.Entity;
import io.github.coffeecatrailway.agameorsomething.common.io.ResourceLoader;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.core.registry.EntityRegistry;
import io.github.coffeecatrailway.agameorsomething.core.registry.RegistrableSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.SomethingRegistry;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import org.agrona.collections.Object2ObjectHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Math;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CoffeeCatRailway
 * Created: 16/11/2022
 */
public class TextureAtlas<T extends RegistrableSomething & HasTexture>
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Path TEMP_ATLAS_PATH = Paths.get("./temp/atlas");
    private static final File TEMP_ATLAS_DIR = TEMP_ATLAS_PATH.toFile();
    private static final Gson GSON = new GsonBuilder().create();

    private static final BufferedImage MISSING_IMAGE;

    static
    {
        try
        {
            MISSING_IMAGE = ImageIO.read(ResourceLoader.getResource(new ObjectLocation("textures/missing.png")));
        } catch (IOException e)
        {
            LOGGER.error(e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static final ObjectLocation MISSING = new ObjectLocation("missing");

    // TODO: Options
    public static final int ATLAS_SIZE = 128;
    public static final boolean REGEN_ATLAS = true;

    public static final TextureAtlas<Tile> TILE_ATLAS = new TextureAtlas<>(TileRegistry.TILES, "tile");
    public static final TextureAtlas<Entity> ENTITY_ATLAS = new TextureAtlas<>(EntityRegistry.ENTITIES, "entity");

    private final SomethingRegistry<T> registry;
    private final String filename;
    private final Object2ObjectHashMap<ObjectLocation, AtlasEntry> entries = new Object2ObjectHashMap<>();

    private Texture atlasTexture;

    private TextureAtlas(SomethingRegistry<T> registry, String filename)
    {
        this.registry = registry;
        this.filename = filename;
    }

    public void init()
    {
        try
        {
            if (!TEMP_ATLAS_DIR.exists())
            {
                Files.createDirectories(TEMP_ATLAS_PATH);
                LOGGER.info("Created 'temp' directory");
            }

            File atlasFile = new File(TEMP_ATLAS_DIR, this.filename + ".png");
            BufferedImage atlas;

            if (!atlasFile.exists() || REGEN_ATLAS)
            {
                Timer.start("atlasGen");
                atlas = this.generateAtlas(atlasFile);

                JsonArray jsonArray = new JsonArray();
                this.entries.values().forEach(entry -> jsonArray.add(entry.serialize(new JsonObject())));
                try (Writer writer = new FileWriter(TEMP_ATLAS_PATH.toString() + "/" + this.filename + ".json"))
                {
                    GSON.toJson(jsonArray, writer);
                }
                LOGGER.debug("Atlas '{}' generation took {}ms", this.filename, Timer.end("atlasGen"));
            } else
            {
                Timer.start("atlasGen");
                atlas = ImageIO.read(atlasFile);
                try (Reader reader = new FileReader(TEMP_ATLAS_PATH.toString() + "/" + this.filename + ".json"))
                {
                    JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);
                    jsonArray.asList().stream().filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject).forEach(obj -> {
                        AtlasEntry entry = new AtlasEntry(obj);
                        this.entries.putIfAbsent(entry.getId(), entry);
                    });
                }
                LOGGER.debug("Atlas '{}' reading took {}ms", this.filename, Timer.end("atlasGen"));
            }

            this.atlasTexture = new Texture(atlas);
        } catch (IOException e)
        {
            LOGGER.error(e);
            e.printStackTrace();
        }
    }

    private BufferedImage generateAtlas(File atlasFile) throws IOException
    {
        // Load textures from registry
        final Map<ObjectLocation, BufferedImage> textures = new HashMap<>();
        textures.put(new ObjectLocation("missing"), MISSING_IMAGE);
        this.registry.foreach((id, obj) -> {
            if (obj.hasTexture())
            {
                try
                {
                    BufferedImage texture = Texture.loadImage(obj.getTextureLocation());
                    if (texture != null)
                        textures.put(obj.getObjectId(), texture);
                } catch (IOException e)
                {
                    LOGGER.error("Something went wrong loading texture for {}", obj, e);
                    e.printStackTrace();
                }
            }
        });

        BufferedImage atlas = new BufferedImage(ATLAS_SIZE, ATLAS_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = atlas.getGraphics();

        // Add textures loaded from registry
        int x, y;
        for (Map.Entry<ObjectLocation, BufferedImage> entry : textures.entrySet())
        {
            BufferedImage image = entry.getValue();
            x = 0;
            y = 0;
            AtlasEntry atlasEntry = new AtlasEntry(entry.getKey(), x, y, image.getWidth(), image.getHeight());
            while (this.entries.values().stream().anyMatch(atlasEntry::isOverlapping))
            {
                x = Math.clamp(0, ATLAS_SIZE - 1, x + 1);
                if (x >= ATLAS_SIZE || x + image.getWidth() > ATLAS_SIZE)
                {
                    x = 0;
                    y = Math.clamp(0, ATLAS_SIZE - 1, y + 1);
                }
                atlasEntry = new AtlasEntry(entry.getKey(), x, y, image.getWidth(), image.getHeight());
            }
            graphics.drawImage(image, x, y, null);
            this.entries.putIfAbsent(entry.getKey(), atlasEntry);
        }

        textures.values().forEach(Image::flush);
        graphics.dispose();

        ImageIO.write(atlas, "PNG", atlasFile);
        return atlas;
    }

    public AtlasEntry getEntry(ObjectLocation id)
    {
        if (!this.entries.containsKey(id))
            return this.entries.get(MISSING);
        return this.entries.get(id);
    }

    public Texture getAtlasTexture()
    {
        return this.atlasTexture;
    }

    public void delete()
    {
        this.entries.values().forEach(AtlasEntry::delete);
        this.entries.clear();
        this.atlasTexture.delete();
    }

    public static void deleteStaticAtlases()
    {
        MISSING_IMAGE.flush();
        TILE_ATLAS.delete();
        ENTITY_ATLAS.delete();
        LOGGER.warn("Static atlases deleted!");
    }
}
