package io.github.coffeecatrailway.agameorsomething.client.render.texture;

import io.github.coffeecatrailway.agameorsomething.common.io.ResourceLoader;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.core.registry.ObjectLocation;
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
import java.io.File;
import java.io.IOException;
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

    private static final BufferedImage MISSING_IMAGE;
    public static final Texture MISSING_TEXTURE;

    // TODO: Options
    public static final int ATLAS_SIZE = 128;
    public static final boolean REGEN_ATLAS = true;

    public static final TextureAtlas<Tile> TILE_ATLAS = new TextureAtlas<>(TileRegistry.TILES, "tiles");

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
        MISSING_TEXTURE = new Texture(MISSING_IMAGE);
    }

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
                atlas = this.generate(atlasFile);
            else
                atlas = ImageIO.read(atlasFile);

            this.atlasTexture = new Texture(atlas);
        } catch (IOException e)
        {
            LOGGER.error(e);
            e.printStackTrace();
        }
    }

    private BufferedImage generate(File atlasFile) throws IOException
    {
        Timer.start("atlasGen");
        // Load textures from registry
        final Map<ObjectLocation, BufferedImage> textures = new HashMap<>();
        textures.put(new ObjectLocation("missing"), MISSING_IMAGE);
        this.registry.foreach((id, obj) -> {
            if (obj.hasTexture())
            {
                try
                {
                    textures.put(obj.getObjectId(), Texture.loadImage(obj.getTextureLocation()));
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

        graphics.dispose();

        ImageIO.write(atlas, "PNG", atlasFile);
        LOGGER.debug("Atlas '{}' generation took {}ms", this.filename, Timer.end("atlasGen"));

        return atlas;
    }

    public Map<ObjectLocation, AtlasEntry> getEntries()
    {
        return this.entries;
    }

    public Texture getAtlasTexture()
    {
        return this.atlasTexture;
    }
}
