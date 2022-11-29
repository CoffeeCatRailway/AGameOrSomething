package io.github.coffeecatrailway.agameorsomething.common.entity;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.common.world.World;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.EntityRegistry;
import org.joml.Math;
import org.joml.Random;
import org.joml.Vector2f;

/**
 * @author CoffeeCatRailway
 * Created: 28/11/2022
 */
public class WanderingEntity extends Entity
{
    private static final Random RANDOM = new Random();

    private final Vector2f nextPos = new Vector2f(0f, 5f);

    public WanderingEntity()
    {
        this(EntityRegistry.WANDERER.get().entityData);
        this.setId(EntityRegistry.WANDERER.get());
    }

    public WanderingEntity(EntityData entityData)
    {
        super(entityData);
    }

    @Override
    public void tick(float delta, AGameOrSomething something, Camera camera, World world)
    {
        super.tick(delta, something, camera, world);

        if (this.position.distance(this.nextPos) < .5f)
            this.pickNextPos(world);

        float x = this.nextPos.x - this.position.x;
        float y = this.nextPos.y - this.position.y;
        float dist = Math.sqrt(x * x + y * y);
        float mult = 5f / dist;
        this.position.add(x * mult * delta, y * mult * delta);
    }

    private void pickNextPos(World world)
    {
        float x = this.random(-world.getWorldRadius() + 1, world.getWorldRadius() - 1);
        float y = this.random(-world.getWorldRadius() + 1, world.getWorldRadius() - 1);
        this.nextPos.set(x, y);
    }

    private float random(float min, float max)
    {
        return min + RANDOM.nextFloat() * (max - min);
    }
}
