package io.github.coffeecatrailway.agameorsomething.client.render;

import io.github.coffeecatrailway.agameorsomething.common.io.ResourceLoader;
import io.github.coffeecatrailway.agameorsomething.core.registry.ObjectLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Locale;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * @author CoffeeCatRailway
 * Created: 14/07/2022
 */
public class Texture
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Texture MISSING = new Texture("missing.png");

    private int id;
    private int width;
    private int height;

    private final ObjectLocation location;

    public Texture(String texture)
    {
        this(new ObjectLocation("textures/" + texture));
    }

    public Texture(ObjectLocation location, String subFolder)
    {
        this(new ObjectLocation(location.getNamespace(), "textures/" + subFolder + "/" + location.getPath()));
    }

    public Texture(ObjectLocation location)
    {
        this.location = location;
        if (!location.getPath().toLowerCase(Locale.ROOT).endsWith(".png"))
            location = new ObjectLocation(location.getNamespace(), location.getPath() + ".png");

        try
        {
            BufferedImage image = ImageIO.read(ResourceLoader.getResource(location));
            this.width = image.getWidth();
            this.height = image.getHeight();

            this.id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, this.id);

            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            ByteBuffer pixels = loadImageToBuffer(image);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        } catch (IOException e)
        {
            LOGGER.error("Something went wrong loading texture {}!", location, e);
            e.printStackTrace();
        }
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public void delete()
    {
        glDeleteTextures(this.id);
        LOGGER.debug("Deleted texture with id {} at {}", this.id, this.location);
    }

    public void bind(int sampler)
    {
        if (sampler >= 0 && sampler <= 31)
        {
            glActiveTexture(GL_TEXTURE0 + sampler);
            glBindTexture(GL_TEXTURE_2D, this.id);
        }
    }

    public static ByteBuffer loadImageToBuffer(final BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixelsRaw = image.getRGB(0, 0, width, height, null, 0, width);
        ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int pixel = pixelsRaw[x * width + y];
                pixels.put((byte) ((pixel >> 16) & 0xFF));  // red
                pixels.put((byte) ((pixel >> 8) & 0xFF));   // green
                pixels.put((byte) (pixel & 0xFF));          // blue
                pixels.put((byte) ((pixel >> 24) & 0xFF));  // alpha
            }
        }
        return pixels.flip();
    }
}
