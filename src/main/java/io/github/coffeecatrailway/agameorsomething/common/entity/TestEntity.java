package io.github.coffeecatrailway.agameorsomething.common.entity;

import io.github.coffeecatrailway.agameorsomething.client.render.texture.atlas.TextureAtlas;
import io.github.coffeecatrailway.agameorsomething.common.entity.ai.SimpleWanderTask;
import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;
import io.github.coffeecatrailway.agameorsomething.core.registry.EntityRegistry;

/**
 * @author CoffeeCatRailway
 * Created: 28/11/2022
 */
public class TestEntity extends Entity
{
    private boolean shouldWander = true;

    public TestEntity(boolean shouldWander)
    {
        this(EntityRegistry.TEST.get().entityData);
        this.setId(EntityRegistry.TEST.get());
        this.shouldWander = shouldWander;
    }

    public TestEntity(EntityData entityData)
    {
        super(entityData);
    }

    @Override
    public void init()
    {
        if (this.shouldWander)
            this.addTask(new SimpleWanderTask(this));
    }

    @Override
    public ObjectLocation getTextureLocation()
    {
        return TextureAtlas.MISSING;
    }
}
