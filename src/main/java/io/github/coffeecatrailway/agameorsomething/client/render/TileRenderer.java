package io.github.coffeecatrailway.agameorsomething.client.render;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.core.registry.ObjectLocation;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import org.joml.Matrix4f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

/**
 * @author CoffeeCatRailway
 * Created: 26/10/2022
 */
public class TileRenderer
{
    private final Map<ObjectLocation, Texture> textureMap = new HashMap<>();
    private final VBOModel model;

    public TileRenderer()
    {
        this.model = this.get2TriangleModel();

        TileRegistry.TILES.foreach((id, tile) -> {
            if (tile.hasTexture() && !this.textureMap.containsKey(tile.getObjectId()))
                this.textureMap.put(tile.getObjectId(), new Texture(tile.getObjectId(), "tiles"));
        });
    }

    /**
     * @return A simple 2 triangle quad
     */
    private VBOModel get2TriangleModel()
    {
        float[] vertices = new float[]
                {
                        -1f, 1f, 0f,    // top left     0
                        1f, 1f, 0f,     // top right    1
                        1f, -1f, 0f,    // bottom right 2
                        -1f, -1f, 0f,   // bottom left  3
                };
        float[] textureCoords = new float[]
                {
                        0f, 0f,
                        1f, 0f,
                        1f, 1f,
                        0f, 1f
                };
        int[] indices = new int[]
                {
                        0, 1, 2,
                        2, 3, 0
                };
        return new VBOModel(vertices, textureCoords, indices);
    }

    /**
     * @return A quad made of two rectangles or 4 triangles
     */
    private VBOModel get2RectangleModel()
    {
        float[] vertices = new float[]
                {
                        -.5f, .5f, 0f,  // top left         0
                        0f, .5f, 0f,    // top middle       1
                        0f, -.5f, 0f,   // bottom middle    2
                        -.5f, -.5f, 0f, // bottom left      3
                        .5f, .5f, 0f,   // top right        4
                        .5f, -.5f, 0f   // bottom right     5
                };
        float[] textureCoords = new float[]
                {
                        0f, 0f,
                        .5f, 0f,
                        .5f, 1f,
                        0f, 1f,
                        1f, 0f,
                        1f, 1f
                };
        int[] indices = new int[]
                {
                        0, 1, 2,
                        2, 3, 0,
                        1, 4, 5,
                        5, 2, 1
                };
        return new VBOModel(vertices, textureCoords, indices);
    }

    /**
     * Render a tile to the world
     *
     * @param tile   {@link Tile} - Tile to be rendered
     * @param pos    {@link Vector2fc} - Position of the tile
     * @param shader {@link Vector2ic}
     * @param camera {@link Camera} - Main camera of the world/game
     */
    public void render(Tile tile, Vector2ic pos, Shader shader, Matrix4f scale, Camera camera)
    {
        if (!tile.hasTexture())
            return;
        Matrix4f targetPos = new Matrix4f().translate(new Vector3f(pos.x() * 2f, pos.y() * 2f, 0f));
        Matrix4f targetProjection = new Matrix4f();
        camera.getProjection().mul(scale, targetProjection);
        targetProjection.mul(targetPos);

        shader.bind();
        this.textureMap.getOrDefault(tile.getObjectId(), Texture.MISSING).bind(0);
        shader.setUniform("tex", 0);
        shader.setUniform("time", (float) glfwGetTime());
        shader.setUniform("projection", targetProjection);
        shader.setUniform("view", camera.getView());
        this.model.render();
        shader.unbind();
    }

    public void delete()
    {
        this.textureMap.values().forEach(Texture::delete);
        this.model.delete();
    }
}
