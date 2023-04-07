package io.github.coffeecatrailway.orsomething.anengine.client.graphics;

import io.github.coffeecatrailway.orsomething.anengine.client.graphics.texture.ITexture;
import io.github.coffeecatrailway.orsomething.anengine.client.graphics.texture.Texture;
import io.github.ocelot.window.Window;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Found on GitHub
 * <a href="https://github.com/mattdesl/lwjgl-basics/blob/552b1f9cbb00000fb4132d6a792af6e0c7476eab/src/mdesl/graphics/glutils/FrameBuffer.java#L58">FrameBuffer.java</a>
 */
public class FBO implements ITexture
{
    private int id;
    private final Texture texture;
    private final boolean ownsTexture;

    FBO(Texture texture, boolean ownsTexture) throws RuntimeException
    {
        this.texture = texture;
        this.ownsTexture = ownsTexture;
        if (!GL.getCapabilities().GL_EXT_framebuffer_object)
            throw new RuntimeException("FBO extension not supported in hardware");

        this.texture.bind(false);
        this.id = glGenFramebuffersEXT();
        glBindFramebufferEXT(GL_FRAMEBUFFER, this.id);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getId(), 0);

        int result = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER);
        if (result != GL_FRAMEBUFFER_COMPLETE)
        {
            glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
            glDeleteFramebuffers(this.id);
            throw new RuntimeException("Exception " + result + " when checking FBO status");
        }
        glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
    }

    /**
     * Creates a framebuffer from pre-existing texture
     * The framebuffer does not "own" the texture and calling dispose() won't destroy the texture.
     *
     * @param texture the texture to use
     * @throws RuntimeException if the framebuffer was not initialized correctly
     */
    public FBO(Texture texture) throws RuntimeException
    {
        this(texture, false);
    }

    public FBO(int width, int height) throws RuntimeException
    {
        this(new Texture(width, height), true);
    }

    public void begin()
    {
        if (this.id == 0)
            throw new IllegalStateException("Can't use FBO as it has been destroyed..");
        glViewport(0, 0, this.getWidth(), this.getHeight());
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, this.id);
    }

    /**
     * @param window Pass in window to get width & height
     */
    public void end(Window window)
    {
        if (this.id == 0)
            return;
        glViewport(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    public void delete()
    {
        if (this.id == 0)
            return;
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        glDeleteFramebuffersEXT(this.id);
        if (this.ownsTexture)
            this.texture.delete();
        this.id = 0;
    }

    public int getId()
    {
        return this.id;
    }

    @Override
    public Texture getTexture()
    {
        return this.texture;
    }

    @Override
    public int getWidth()
    {
        return this.texture.getWidth();
    }

    @Override
    public int getHeight()
    {
        return this.texture.getHeight();
    }

    @Override
    public float getU0()
    {
        return 0f;
    }

    @Override
    public float getV0()
    {
        return 1f;
    }

    @Override
    public float getU1()
    {
        return 1f;
    }

    @Override
    public float getV1()
    {
        return 0f;
    }
}
