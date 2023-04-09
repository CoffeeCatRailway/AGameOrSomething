package io.github.coffeecatrailway.orsomething.anengine.client.graphics.texture;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.orsomething.anengine.core.io.ObjectLocation;
import io.github.coffeecatrailway.orsomething.anengine.core.io.ResourceLoader;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20C.GL_MAX_TEXTURE_IMAGE_UNITS;

/**
 * @author CoffeeCatRailway
 * Created: 14/07/2022
 */
public class Texture implements ITexture
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int MAX_TEXTURES = glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS);

    private int id;
    private final int width;
    private final int height;

    public Texture(BufferedImage image)
    {
        this.width = image.getWidth();
        this.height = image.getHeight();

        this.id = glGenTextures();
        this.bind(false);
        this.setFilter(GL_NEAREST);

        ByteBuffer pixels = loadImageToBuffer(image);
        this.upload(GL_RGBA, pixels);
    }

    public Texture(int width, int height)
    {
        glEnable(GL_TEXTURE_2D);

        this.width = width;
        this.height = height;

        this.id = glGenTextures();
        this.bind(false);
        this.setFilter(GL_NEAREST);

        ByteBuffer empty = BufferUtils.createByteBuffer(this.width * this.height * 4);
        this.upload(GL_RGBA, empty);
    }

    protected void setUnpackAlignment()
    {
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glPixelStorei(GL_PACK_ALIGNMENT, 1);
    }

    public void upload(int format, ByteBuffer data)
    {
        this.bind(false);
        this.setUnpackAlignment();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, format, GL_UNSIGNED_BYTE, data);
    }

    public void setFilter(int filter)
    {
        this.bind(false);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
    }

    public void delete()
    {
        if (this.valid())
        {
            glDeleteTextures(this.id);
            LOGGER.debug("Deleted texture with id {}", this.id);
            this.id = 0;
        }
    }

    public void bind(boolean active)
    {
        this.bind(active, this.id);
    }

    public void bind(boolean active, int sampler)
    {
        if (!this.valid())
            throw new IllegalStateException("Trying to bind a texture that was disposed");
        if (sampler >= 0 && sampler < MAX_TEXTURES)
        {
            if (active)
                glActiveTexture(GL_TEXTURE0 + sampler);
            glBindTexture(GL_TEXTURE_2D, this.id);
        }
    }

    public boolean valid()
    {
        return this.id != 0;
    }

    public int getId()
    {
        return this.id;
    }

    @Override
    public Texture getTexture()
    {
        return this;
    }

    @Override
    public int getWidth()
    {
        return this.width;
    }

    @Override
    public int getHeight()
    {
        return this.height;
    }

    @Override
    public float getU0()
    {
        return 0f;
    }

    @Override
    public float getV0()
    {
        return 0f;
    }

    @Override
    public float getU1()
    {
        return 1f;
    }

    @Override
    public float getV1()
    {
        return 1f;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Texture texture = (Texture) o;
        return id == texture.id && width == texture.width && height == texture.height;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, width, height);
    }

    public static BufferedImage loadImage(ObjectLocation location) throws IOException
    {
        if (!location.getPath().toLowerCase(Locale.ROOT).endsWith(".png"))
            location = new ObjectLocation(location.getNamespace(), location.getPath() + ".png");
        try
        {
            return ImageIO.read(ResourceLoader.getResource(location));
        } catch (IllegalArgumentException e)
        {
            LOGGER.error("Texture {} failed to load!", location, e);
            return null;
        }
    }

    public static ByteBuffer loadImageToBuffer(final BufferedImage image)
    {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));      // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));       // Green component
                buffer.put((byte) (pixel & 0xFF));              // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));      // Alpha component. Only for RGBA
            }
        }

        buffer.flip();
        return buffer;
    }
}
