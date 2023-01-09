package io.github.coffeecatrailway.agameorsomething.common.entity;

import io.github.coffeecatrailway.agameorsomething.client.camera.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.BatchRenderer;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.Animation;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.HasAnimation;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.atlas.TextureAtlas;
import io.github.coffeecatrailway.agameorsomething.common.world.World;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.EntityRegistry;
import io.github.ocelot.window.input.KeyboardHandler;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author CoffeeCatRailway
 * Created: 15/11/2022
 */
public class PlayerEntity extends Entity implements HasAnimation
{
    private static final float WALK_SPED = 10f;

    private final Animation idleAnimation;
    private final Animation walkUpAnimation;
    private final Animation walkDownAnimation;
    private final Animation walkSideAnimation;
    private Animation currentAnimation;
    private boolean flipHorizontal = false;

    public PlayerEntity()
    {
        this(EntityRegistry.PLAYER.get().entityData);
        this.setId(EntityRegistry.PLAYER.get());
    }

    public PlayerEntity(EntityData data)
    {
        super(data);
        this.idleAnimation = new Animation("player_idle", "entity", 4);//.frameOrder(0, 1, 2, 3, 0, 2);
        this.walkUpAnimation = new Animation("player_walk_up", "entity", 3).speed(.75f).frameOrder(0, 1, 0, 2);
        this.walkDownAnimation = new Animation("player_walk_down", "entity", 3).speed(.75f).frameOrder(0, 1, 0, 2);
        this.walkSideAnimation = new Animation("player_walk_side", "entity", 3).speed(.75f).frameOrder(0, 1, 0, 2);
        this.currentAnimation = this.idleAnimation;
    }

    @Override
    public void tick(float delta, AGameOrSomething something, Camera camera, World world)
    {
        super.tick(delta, something, camera, world);

        this.currentAnimation = this.idleAnimation;
        this.flipHorizontal = false;

        KeyboardHandler keyboardHandler = something.getKeyboardHandler();
        if (keyboardHandler.isKeyPressed(GLFW_KEY_W))
        {
            this.position.y += WALK_SPED * delta;
            this.currentAnimation = this.walkUpAnimation;
        }
        if (keyboardHandler.isKeyPressed(GLFW_KEY_S))
        {
            this.position.y -= WALK_SPED * delta;
            this.currentAnimation = this.walkDownAnimation;
        }
        if (keyboardHandler.isKeyPressed(GLFW_KEY_A))
        {
            this.position.x -= WALK_SPED * delta;
            this.currentAnimation = this.walkSideAnimation;
            this.flipHorizontal = true;
        }
        if (keyboardHandler.isKeyPressed(GLFW_KEY_D))
        {
            this.position.x += WALK_SPED * delta;
            this.currentAnimation = this.walkSideAnimation;
        }

        this.currentAnimation.tick();

        if (keyboardHandler.isKeyPressed(GLFW_KEY_C))
            this.position.set(0f);
    }

    @Override
    public void render(AGameOrSomething something, BatchRenderer batch)
    {
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(TextureAtlas.ENTITY_ATLAS.getEntry(this.currentAnimation.getCurrentFrame()), this.position.x + (this.flipHorizontal ? 1f : 0f), this.position.y, this.flipHorizontal ? -1f : 1f, 2f);
        batch.end();
//        LineRenderer.drawBoundingBox(this.boundingBox);
    }

    @Override
    public Animation[] getAnimations()
    {
        return new Animation[] {this.idleAnimation, this.walkUpAnimation, this.walkDownAnimation, this.walkSideAnimation};
    }
}
