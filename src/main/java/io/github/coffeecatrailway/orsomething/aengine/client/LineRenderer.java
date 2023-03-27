package io.github.coffeecatrailway.orsomething.aengine.client;

import io.github.coffeecatrailway.orsomething.aengine.client.camera.Camera;
import io.github.coffeecatrailway.orsomething.aengine.client.shader.Shader;
import io.github.coffeecatrailway.orsomething.aengine.client.shader.ShaderAttribute;
import io.github.coffeecatrailway.orsomething.aengine.common.collision.BoundingBox;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;

/**
 * @author CoffeeCatRailway
 * Created: 17/01/2023
 */
public class LineRenderer
{
    public static final LineRenderer INSTANCE = new LineRenderer();
    public static final Shader SHADER = new Shader("line_render");
    public static int renderCalls = 0;

    private final VAO vao;

    private int index;
    private final int maxIndex;
    private boolean drawing = false;

    public LineRenderer()
    {
        this(1000);
    }

    public LineRenderer(int size)
    {
        this.vao = new VAO(size, new ShaderAttribute("position", 2, GL_FLOAT));
        this.maxIndex = size;
    }

    /**
     * Updates uniforms for projection, view matrices
     *
     * @param camera {@link Camera} - Main camera
     */
    public void updateUniforms(Camera camera)
    {
        SHADER.bind();
        SHADER.setUniformMatrix4f("uProjection", camera.getProjectionMatrix());
        SHADER.setUniformMatrix4f("uView", camera.getViewMatrix());
    }

    public void begin(float r, float g, float b)
    {
        if (this.drawing)
            throw new IllegalStateException("Must not be drawing before `begin()` is called!");
        this.drawing = true;
        SHADER.bind();
        SHADER.setUniformVector3f("uColor", r, g, b);
        this.vao.bind();
        this.vao.bindWrite();
        this.index = 0;
        renderCalls = 0;
    }

    public void end()
    {
        if (!this.drawing)
            throw new IllegalStateException("Must be drawing before `end()` is called!");
        this.drawing = false;
        this.flush();
        this.vao.unbind();
        SHADER.unbind();
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

    public void drawBox(BoundingBox box)
    {
        drawBox(box.getPosition(), box.getPosition().add(box.getBounds(), new Vector2f()));
    }

    public void drawBox(Vector2fc bottomLeft, Vector2fc topRight)
    {
        draw(bottomLeft.x(), bottomLeft.y(),
                topRight.x(), bottomLeft.y(),
                topRight.x(), topRight.y(),
                bottomLeft.x(), topRight.y(),
                bottomLeft.x(), bottomLeft.y());
    }

    public void drawBox(float x1, float y1, float x2, float y2)
    {
        draw(x1, y1, x2, y1, x2, y2, x1, y2, x1, y1);
    }

    public void drawLine(Vector2fc start, Vector2fc end)
    {
        draw(start.x(), start.y(), end.x(), end.y());
    }

    public void draw(float... vertices)
    {
        if (!this.drawing)
            throw new IllegalStateException("Must be drawing before `draw()` is called!");

        // Check flush
        if (this.index >= this.maxIndex)
            this.flush();

        // Add vertices to vao
        for (int i = 0; i < vertices.length; i += 2)
            this.vertex(vertices[i], vertices[i + 1]);
    }

    private void vertex(float x, float y)
    {
        this.vao.put(this.index, buffer -> buffer.putFloat(x).putFloat(y));
        this.index++;
    }

    private void render()
    {
        this.vao.draw(GL_LINE_STRIP, 0, this.index);
        renderCalls++;
    }
}
