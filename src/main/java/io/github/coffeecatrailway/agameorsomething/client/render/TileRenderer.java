package io.github.coffeecatrailway.agameorsomething.client.render;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.Texture;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import io.github.coffeecatrailway.agameorsomething.core.registry.ObjectLocation;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

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
//    private final VBOModel model;

    public TileRenderer()
    {
//        this.model = VBOModel.simple1x1Model();

        TileRegistry.TILES.foreach((id, tile) -> {
            if (tile.hasTexture() && !this.textureMap.containsKey(tile.getObjectId()))
                this.textureMap.put(tile.getObjectId(), tile.getCustomTexture() != null ? tile.getCustomTexture() : new Texture(tile.getObjectId(), "tile"));
        });
    }

    /**
     * Renders a tile to specified position on a set grid
     *
     * @param tile   {@link Tile} - Tile to be rendered
     * @param pos    {@link Vector2ic} - Position of the tile
     * @param shader {@link Shader}
     * @param camera {@link Camera} - Main camera of the world/game
     */
    public void renderOnGrid(Tile tile, Vector2ic pos, Shader shader, Camera camera)
    {
        if (!tile.hasTexture() || !tile.isVisible())
            return;
        this.render(tile, shader, camera.getProjectionMatrix(), camera.getViewMatrix().translate(pos.x(), pos.y(), 0f));
    }

    /**
     * Renders a tile to specified position
     *
     * @param tile   {@link Tile} - Tile to be rendered
     * @param pos    {@link Vector2fc} - Position of the tile
     * @param shader {@link Shader}
     * @param camera {@link Camera} - Main camera of the world/game
     */
    public void renderOffGrid(Tile tile, Vector2fc pos, Shader shader, Camera camera)
    {
        if (!tile.hasTexture() || !tile.isVisible())
            return;
        Matrix4f targetPos = new Matrix4f().translate(pos.x(), pos.y(), 0f);
        Matrix4f targetProjection = new Matrix4f(camera.getProjectionMatrix());
        targetProjection.mul(targetPos);

        this.render(tile, shader, targetProjection, camera.getViewMatrix());
    }

    /**
     * Renders a tile
     *
     * @param tile       {@link Tile} - Tile to be rendered
     * @param shader     {@link Shader}
     * @param projection {@link Matrix4f}
     * @param view       {@link Matrix4f}
     */
    public void render(Tile tile, Shader shader, Matrix4fc projection, Matrix4fc view)
    {
        shader.bind();
        this.textureMap.getOrDefault(tile.getObjectId(), Texture.MISSING).bind(0);
        shader.setUniform("tex", 0);
        shader.setUniform("time", (float) glfwGetTime());
        shader.setUniform("projection", projection);
        shader.setUniform("view", view);
//        this.model.render();
        tile.getModel().render();
        shader.unbind();
    }

    public void delete()
    {
        this.textureMap.values().stream().filter(tex -> !tex.equals(Texture.MISSING)).forEach(Texture::delete);
//        this.model.delete();
    }
}
