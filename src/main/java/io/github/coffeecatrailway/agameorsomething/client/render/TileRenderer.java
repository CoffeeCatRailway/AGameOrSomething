package io.github.coffeecatrailway.agameorsomething.client.render;

import io.github.coffeecatrailway.agameorsomething.client.camera.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.shader.Shader;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.atlas.TextureAtlas;
import io.github.coffeecatrailway.agameorsomething.common.tile.Tile;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

/**
 * @author CoffeeCatRailway
 * Created: 26/10/2022
 * <p>
 * Deprecated because of batch rendering
 * Still usable but, not recomended
 */
@Deprecated
public class TileRenderer
{
    public static final TileRenderer RENDERER = new TileRenderer();

    private TileRenderer()
    {
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
        TextureAtlas.TILE_ATLAS.getAtlasTexture().bind(0);
        shader.setUniformi("tex", 0);
//        shader.setUniform("time", (float) glfwGetTime());
        shader.setUniformMatrix4f("projection", projection);
        shader.setUniformMatrix4f("view", view);
        shader.setUniformVector4f("uvCoords", TextureAtlas.TILE_ATLAS.getEntry(tile.getObjectId()).getUVCoords());
        tile.getModel().render();
        shader.unbind();
    }
}
