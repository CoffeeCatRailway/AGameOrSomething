package io.github.coffeecatrailway.agameorsomething.common.entity;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.Shader;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.Texture;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.TextureAtlas;
import io.github.coffeecatrailway.agameorsomething.client.render.vbo.VBOModel;
import io.github.coffeecatrailway.agameorsomething.client.render.vbo.VBOModels;
import io.github.coffeecatrailway.agameorsomething.common.world.World;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

/**
 * @author CoffeeCatRailway
 * Created: 15/11/2022
 */
public abstract class Entity
{
    protected VBOModel model;
    protected Texture texture;

    protected Vector2f position; // TODO: Have `lastPosition` for collision box updating

    public Entity()
    {
        this(VBOModels.SIMPLE_1X1, TextureAtlas.MISSING_TEXTURE, new Vector2f());
    }

    public Entity(VBOModel model, Texture texture, Vector2f position)
    {
        this.model = model;
        this.texture = texture;
        this.position = position;
    }

    public abstract void tick(float delta, AGameOrSomething something, Camera camera, World world);

    public void render(Shader shader, Camera camera)
    {
        Matrix4f targetPos = new Matrix4f().translate(this.position.x, this.position.y, 0f);
        Matrix4f targetProjection = new Matrix4f(camera.getProjectionMatrix());
        targetProjection.mul(targetPos);

        shader.bind();
        this.texture.bind(0);
        shader.setUniform("tex", 0);
        shader.setUniform("time", (float) glfwGetTime());
        shader.setUniform("projection", targetProjection);
        shader.setUniform("view", camera.getViewMatrix());
        this.model.render();
        shader.unbind();
    }
}
