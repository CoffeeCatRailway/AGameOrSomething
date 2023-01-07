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
public class SimpleWanderTask extends Task
{
    private final Vector2f nextPos = new Vector2f(0f);

    public SimpleWanderTask(Entity entity)
    {
        super(entity);
    }

    @Override
    public void tick(float delta, World world)
    {
        if (this.entity.getPosition().distance(this.nextPos) < .5f)
            this.pickNextPos(world);

        float x = this.nextPos.x - this.entity.getPosition().x;
        float y = this.nextPos.y - this.entity.getPosition().y;
        float dist = Math.sqrt(x * x + y * y);
        float mult = 5f / dist;
        this.entity.getPosition().add(x * mult * delta, y * mult * delta);
    }

    private void pickNextPos(World world)
    {
        float x = MatUtils.randomFloat(world.random(), -world.getWorldRadius() + 2, world.getWorldRadius() - 2);
        float y = MatUtils.randomFloat(world.random(), -world.getWorldRadius() + 2, world.getWorldRadius() - 2);
        this.nextPos.set(x, y);
    }
}
