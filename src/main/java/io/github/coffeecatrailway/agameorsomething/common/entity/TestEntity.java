package io.github.coffeecatrailway.agameorsomething.common.entity;

import io.github.coffeecatrailway.agameorsomething.client.render.BatchRenderer;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.atlas.TextureAtlas;
import io.github.coffeecatrailway.agameorsomething.common.entity.ai.PathFinderTask;
import io.github.coffeecatrailway.agameorsomething.common.entity.ai.SimpleWanderTask;
import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.EntityRegistry;

/**
 * @author CoffeeCatRailway
 * Created: 28/11/2022
 */
public class TestEntity extends Entity
{
    private AI ai = AI.NONE;
    private PathFinderTask pathFinderTask;

    public TestEntity(AI ai)
    {
        this(EntityRegistry.TEST.get().entityData);
        this.setId(EntityRegistry.TEST.get());
        this.ai = ai;
    }

    public TestEntity(EntityData entityData)
    {
        super(entityData);
    }

    @Override
    public void init()
    {
        switch (this.ai)
        {
            case WANDER -> this.addTask(new SimpleWanderTask(this, 20f, 5f));
            case A_STAR -> this.addTask(this.pathFinderTask = new PathFinderTask(this, 20, 15f, 10f, 15f));
            case NONE -> {}
        }
    }

    @Override
    public void render(AGameOrSomething something, BatchRenderer batch)
    {
        super.render(something, batch);
        if (this.ai == AI.A_STAR)
            this.pathFinderTask.renderDebug();
    }

    @Override
    public ObjectLocation getTextureLocation()
    {
        return TextureAtlas.MISSING;
    }

    public enum AI
    {
        WANDER, A_STAR, NONE
    }
}
