package io.github.coffeecatrailway.agameorsomething.common.entity.ai;

import io.github.coffeecatrailway.agameorsomething.common.entity.Entity;
import io.github.coffeecatrailway.agameorsomething.common.utils.MatUtils;
import io.github.coffeecatrailway.agameorsomething.common.world.World;
import org.joml.Math;
import org.joml.Vector2f;

/**
 * @author CoffeeCatRailway
 * Created: 07/01/2023
 */
public class TestWanderTask extends Task
{
    private final Vector2f destination = new Vector2f(0f);
    private final float wanderRadius;

    public TestWanderTask(Entity entity, float wanderRadius)
    {
        super(entity);
        this.wanderRadius = wanderRadius;
    }

    @Override
    public void tick(float delta, World world)
    {
        if (this.entity.getPosition().distance(this.destination) < .5f)
            this.chooseDestination(world);

        float x = this.destination.x - this.entity.getPosition().x;
        float y = this.destination.y - this.entity.getPosition().y;
        float dist = Math.sqrt(x * x + y * y);
        float mult = 5f / dist;
        this.entity.getPosition().add(x * mult * delta, y * mult * delta);
    }

    private void chooseDestination(World world)
    {
        float x = MatUtils.randomFloat(world.random(), -this.wanderRadius, this.wanderRadius);
        float y = MatUtils.randomFloat(world.random(), -this.wanderRadius, this.wanderRadius);
        this.destination.set(entity.getPosition()).add(x, y);
    }
}
