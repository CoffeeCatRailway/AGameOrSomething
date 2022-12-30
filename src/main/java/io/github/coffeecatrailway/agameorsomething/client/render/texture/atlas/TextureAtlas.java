package io.github.coffeecatrailway.agameorsomething.client.render.texture.atlas;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.Animation;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.HasAnimation;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.HasTexture;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.Texture;
import io.github.coffeecatrailway.agameorsomething.common.io.ResourceLoader;
import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.core.registry.EntityRegistry;
import io.github.coffeecatrailway.agameorsomething.core.registry.RegistrableSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.SomethingRegistry;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import org.agrona.collections.Object2ObjectHashMap;
import org.joml.Math;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11C.*;

/**
 * @author CoffeeCatRailway
 * Created: 16/11/2022
 */
public class TextureAtlas
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Path TEMP_ATLAS_PATH = Paths.get("./temp/atlas");
    private static final File TEMP_ATLAS_DIR = TEMP_ATLAS_PATH.toFile();
    private static final Gson GSON = new GsonBuilder().create();

    private static final BufferedImage MISSING_IMAGE;

    static
    {
        MISSING_IMAGE = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 16; ++y)
        {
            for (int x = 0; x < 16; ++x)
            {
                if (y < 8 ^ x < 8)
                {
                    MISSING_IMAGE.setRGB(x, y, 0xFFF800F8);
                } else
                {
                    MISSING_IMAGE.setRGB(x, y, 0xFF000000);
                }
            }
        }
    }

    public static final ObjectLocation MISSING = new ObjectLocation("missing");

    private static final int MAX_TEXTURE_SIZE = getMaxTextureSize();
    public static final boolean REGEN_ATLAS = true;

    public static final TextureAtlas TILE_ATLAS = new TextureAtlas(TileRegistry.TILES, "tile");
    public static final TextureAtlas ENTITY_ATLAS = new TextureAtlas(EntityRegistry.ENTITIES, "entity");
    public static final TextureAtlas PARTICLE_ATLAS = new TextureAtlas(new ObjectLocation("textures/particle/textures.json"), "particle");

    private final Consumer<Map<ObjectLocation, ObjectLocation>> toLoad;
    private final String filename;
    private final STBStitcher<ObjectLocation> stitcher;
    private final Object2ObjectHashMap<ObjectLocation, AtlasEntry> entries = new Object2ObjectHashMap<>();

    private Texture atlasTexture;

    private <T extends RegistrableSomething & HasTexture> TextureAtlas(SomethingRegistry<T> registry, String filename)
    {
        this.toLoad = texturePaths -> registry.values().forEach(obj -> {
            if (obj.hasTexture())
            {
                if (obj instanceof HasAnimation anim)
                {
                    for (Animation anim1 : anim.getAnimations())
                        for (ObjectLocation path : anim1.getBaseFrames())
                            texturePaths.put(path, path);
                } else
                    texturePaths.put(obj.getObjectId(), obj.getTextureLocation());
            }
        });
        this.filename = filename;
        this.stitcher = new STBStitcher<>(MAX_TEXTURE_SIZE, MAX_TEXTURE_SIZE, 0);
    }

    private TextureAtlas(ObjectLocation jsonPath, String filename)
    {
        this.toLoad = texturePaths -> {
            JsonArray jsonArray = GSON.fromJson(ResourceLoader.readToString(jsonPath), JsonArray.class);
            jsonArray.asList().stream().filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject).forEach(obj ->
                    texturePaths.put(new ObjectLocation(obj.get("id")), new ObjectLocation(obj.get("texture"))));
        };
        this.filename = filename;
        this.stitcher = new STBStitcher<>(MAX_TEXTURE_SIZE, MAX_TEXTURE_SIZE, 0);
    }

    private static int getMaxTextureSize()
    {
        int maxTextureSize = glGetInteger(GL_MAX_TEXTURE_SIZE);

        for (int size = Math.max(32768, maxTextureSize); size >= 1024; size >>= 1)
        {
            // Proxy image 2D is used to verify the graphics driver can actually handle what it reports it can
            glTexImage2D(GL_PROXY_TEXTURE_2D, 0, GL_RGBA, size, size, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
            int k = glGetTexLevelParameteri(GL_PROXY_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
            if (k != 0)
                return size;
        }

        maxTextureSize = Math.max(maxTextureSize, 1024);
        LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", maxTextureSize);
        return maxTextureSize;
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
                LOGGER.info("Atlas '{}' generation took {}ms", this.filename, Timer.end("atlasGen"));
            } else
            {
                Timer.start("atlasLoad");
                atlas = ImageIO.read(atlasFile);
                try (Reader reader = new FileReader(TEMP_ATLAS_PATH.toString() + "/" + this.filename + ".json"))
                {
                    JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);
                    jsonArray.asList().stream().filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject).forEach(obj -> {
                        AtlasEntry entry = new AtlasEntry(obj, this);
                        this.entries.putIfAbsent(entry.getId(), entry);
                    });
                }
                LOGGER.info("Atlas '{}' loaded in {}ms", this.filename, Timer.end("atlasLoad"));
            }

            this.atlasTexture = new Texture(atlas);
        } catch (IOException e)
        {
            LOGGER.error("Failed to generate/load atlas", e);
        }
    }

    private BufferedImage generateAtlas(File atlasFile) throws IOException
    {
        // Generate texture ids & paths
        final Map<ObjectLocation, ObjectLocation> texturePaths = new HashMap<>();
        this.toLoad.accept(texturePaths);
//        extraTextures.accept(texturePaths); TODO: Use json file 'extra.json' in directory with same name as filename

        // Load textures from registry
        final Map<ObjectLocation, BufferedImage> textures = new HashMap<>();
        textures.put(MISSING, MISSING_IMAGE);
        this.stitcher.add(MISSING, MISSING_IMAGE.getWidth(), MISSING_IMAGE.getHeight());
        texturePaths.forEach((id, path) -> {
            try
            {
                BufferedImage texture = Texture.loadImage(path);
                if (texture != null)
                {
                    textures.put(id, texture);
                    this.stitcher.add(id, texture.getWidth(), texture.getHeight());
                }
            } catch (IOException e)
            {
                LOGGER.error("Something went wrong loading texture for {}", id, e);
                e.printStackTrace();
            }
        });
        texturePaths.clear();
        this.stitcher.stitch();

        BufferedImage atlas = new BufferedImage(this.stitcher.getWidth(), this.stitcher.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = atlas.getGraphics();

        // Add textures loaded from registry
        this.stitcher.walk((entry, x, y, width, height) -> {
            graphics.drawImage(textures.get(entry), x, y, null);
            this.entries.putIfAbsent(entry, new AtlasEntry(entry, this, x, y, width, height));
        });

        textures.values().forEach(Image::flush);
        textures.clear();
        graphics.dispose();

        ImageIO.write(atlas, "PNG", atlasFile);
        return atlas;
    }

    public AtlasEntry getEntry(ObjectLocation id)
    {
        if (!this.has(id))
            return this.entries.get(MISSING);
        return this.entries.get(id);
    }

    public boolean has(ObjectLocation id)
    {
        return this.entries.containsKey(id);
    }

    public Texture getAtlasTexture()
    {
        return this.atlasTexture;
    }

    public int getWidth()
    {
        return this.stitcher.getWidth();
    }

    public int getHeight()
    {
        return this.stitcher.getHeight();
    }

    public void delete()
    {
        this.entries.clear();
        this.atlasTexture.delete();
        this.stitcher.free();
    }

    public static void deleteStaticAtlases()
    {
        MISSING_IMAGE.flush();
        TILE_ATLAS.delete();
        ENTITY_ATLAS.delete();
        PARTICLE_ATLAS.delete();
        LOGGER.warn("Static atlases deleted!");
    }
}
