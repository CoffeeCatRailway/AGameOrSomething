package io.github.coffeecatrailway.agameorsomething.common.entity;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.common.io.Window;
import io.github.coffeecatrailway.agameorsomething.common.world.World;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author CoffeeCatRailway
 * Created: 15/11/2022
 */
public class PlayerEntity extends Entity
{
    private static final Vector2f LERP_CAMERA = new Vector2f();
    private static final float CAMERA_SMOOTHNESS = .15f;

    private static final float WALK_SPED = 10f;

    public PlayerEntity()
    {
        super();
    }

    @Override
    public void tick(float delta, AGameOrSomething something, Camera camera, World world)
    {
        if (Window.getInputHandler().isKeyDown(GLFW_KEY_A))
            this.position.x -= WALK_SPED * delta;
        if (Window.getInputHandler().isKeyDown(GLFW_KEY_D))
            this.position.x += WALK_SPED * delta;

        if (Window.getInputHandler().isKeyDown(GLFW_KEY_W))
            this.position.y += WALK_SPED * delta;
        if (Window.getInputHandler().isKeyDown(GLFW_KEY_S))
            this.position.y -= WALK_SPED * delta;

        if (Window.getInputHandler().isKeyDown(GLFW_KEY_C))
            this.position.set(0f);

        camera.setPosition(camera.getPosition().lerp(this.position, CAMERA_SMOOTHNESS, LERP_CAMERA));
    }
}
