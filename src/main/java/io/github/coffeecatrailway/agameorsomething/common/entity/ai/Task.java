package io.github.coffeecatrailway.agameorsomething.common.entity.ai;

import io.github.coffeecatrailway.agameorsomething.common.entity.Entity;
import io.github.coffeecatrailway.agameorsomething.common.world.World;

/**
 * @author CoffeeCatRailway
 * Created: 07/01/2023
 */
public abstract class Task
{
    protected Entity entity;

    public Task(Entity entity)
    {
        this.entity = entity;
    }

    public abstract void tick(float delta, World world);
}
