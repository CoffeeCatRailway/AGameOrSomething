package io.github.coffeecatrailway.agameorsomething.client.render;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.shader.Shader;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.AtlasEntry;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.Texture;
import io.github.coffeecatrailway.agameorsomething.client.render.vbo.VAO;
import org.joml.Vector4fc;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

/**
 * @author CoffeeCatRailway & Ocelot
 * Created: 23/11/2022
 */
public class BatchRenderer
{
    public static int renderCalls = 0;

    private Texture texture;
    private Shader shader;

    private final VAO vao;

    private int index;
    private final int maxIndex;
    private float r = 1f, g = 1f, b = 1f, a = 1f;
    private boolean drawing = false;

    public BatchRenderer()
    {
        this(1000);
    }

    public BatchRenderer(int size)
    {
        this(Shader.SIMPLE, size);
    }

    public BatchRenderer(Shader shader)
    {
        this(shader, 1000);
    }

    public BatchRenderer(Shader shader, int size)
    {
        this.shader = shader;
        this.vao = new VAO(size * 6, Shader.DEFAULT_ATTRIBUTES);
        this.maxIndex = size * 6;
    }

    /**
     * @param hex Color in hex format
     */
    public void setColor(int hex)
    {
        this.setColor(hex, false);
    }

    /**
     * @param hex Color in hex format
     * @param hasAlpha True if has value includes alpha
     */
    public void setColor(int hex, boolean hasAlpha)
    {
        this.a = hasAlpha ? ((hex >> 24) & 0xFF) / 255f : 1f;
        this.r = ((hex >> 16) & 0xFF) / 255f;
        this.g = ((hex >> 8) & 0xFF) / 255f;
        this.b = (hex & 0xFF) / 255f;
    }

    /**
     * @param r Red channel
     * @param g Green channel
     * @param b Blue channel
     * @param a Alpha channel
     */
    public void setColor(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * Updates uniforms for projection, view & texture sampler
     * Uses base shader set in constructor
     *
     * @param camera {@link Camera} - Main camera
     */
    public void updateUniforms(Camera camera)
    {
        this.updateUniforms(camera, this.shader);
    }

    /**
     * Updates uniforms for projection, view & texture sampler
     *
     * @param camera {@link Camera} - Main camera
     * @param shader {@link Shader} - Shader to be used
     */
    public void updateUniforms(Camera camera, Shader shader)
    {
        shader.bind();
        shader.setUniformi("tex", 0);
//        shader.setUniform("time", (float) glfwGetTime());
        shader.setUniformMatrix4f("projection", camera.getProjectionMatrix());
        shader.setUniformMatrix4f("view", camera.getViewMatrix());
    }

    public Shader getShader()
    {
        return this.shader;
    }

    public void setShader(Shader shader, Camera camera)
    {
        this.setShader(shader, true, camera);
    }

    /**
     * @param shader         {@link Shader} - Shader to be used
     * @param updateUniforms {@link Boolean} - If uniforms should be updated
     * @param camera         {@link Camera} - Main camera
     */
    public void setShader(Shader shader, boolean updateUniforms, Camera camera)
    {
        if (shader == null)
            throw new NullPointerException("Shader cannot be null");
        if (this.drawing)
            this.flush();
        this.shader = shader;
        if (updateUniforms)
            this.updateUniforms(camera);
        else if (this.drawing)
            this.shader.bind();
    }

    public void begin()
    {
        if (this.drawing)
            throw new IllegalStateException("Must not be drawing before `begin()` is called!");
        this.drawing = true;
        this.shader.bind();
        this.vao.bind();
        this.vao.bindWrite();
        this.index = 0;
        renderCalls = 0;
        this.texture = null;
    }

    public void end()
    {
        if (!this.drawing)
            throw new IllegalStateException("Must be drawing before `end()` is called!");
        this.drawing = false;
        this.flush();
        this.vao.unbind();
        this.shader.unbind();
    }

    /**
     * Renders data if any was entered
     */
    public void flush()
    {
        if (this.index > 0)
        {
            this.render();
            this.index = 0;
        }
    }

    /**
     * Draw atlas entry at x,y with width & height
     * @param entry {@link AtlasEntry} - Entry to be rendered
     */
    public void draw(AtlasEntry entry, float x, float y, float width, float height)
    {
        Vector4fc uv = entry.getUVCoords();
        this.draw(entry.getAtlas().getAtlasTexture(), x, y, width, height, uv.x(), uv.y(), uv.x() + uv.z(), uv.y() + uv.w());
    }

    /**
     * Draw atlas entry at x,y with width & height
     * u,v is the top left of the sprite with u2,v2 being the bottom right
     * @param texture {@link Texture} - Texture to be rendered
     */
    public void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2)
    {
        this.checkFlush(texture);

//        float x1 = x;
//        float y1 = y;

        float x2 = x + width;
//        float y2 = y;

        float x3 = x + width;
        float y3 = y + height;

//        float x4 = x;
        float y4 = y + height;

        // top left, top right, bottom left
        this.vertex(x, y, this.r, this.g, this.b, this.a, u, v2);
        this.vertex(x2, y, this.r, this.g, this.b, this.a, u2, v2);
        this.vertex(x, y4, this.r, this.g, this.b, this.a, u, v);

        // top right, bottom right, bottom left
        this.vertex(x2, y, this.r, this.g, this.b, this.a, u2, v2);
        this.vertex(x3, y3, this.r, this.g, this.b, this.a, u2, v);
        this.vertex(x, y4, this.r, this.g, this.b, this.a, u, v);
    }

    private void vertex(float x, float y, float r, float g, float b, float a, float u, float v)
    {
        this.vao.put(this.index, buffer -> buffer.putFloat(x).putFloat(y).putFloat(0f).putFloat(u).putFloat(v).putFloat(r).putFloat(g).putFloat(b).putFloat(a));
        this.index++;
    }

    protected void checkFlush(Texture texture)
    {
        if (texture == null)
            throw new NullPointerException("Texture cannot be null");

        if (texture != this.texture || this.index >= this.maxIndex)
        {
            this.flush();
            this.texture = texture;
        }
    }

    private void render()
    {
        if (this.texture != null)
            this.texture.bind(0);
        this.vao.draw(GL_TRIANGLES, 0, this.index);
        renderCalls++;
    }
}
