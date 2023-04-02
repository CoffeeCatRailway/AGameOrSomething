package io.github.coffeecatrailway.orsomething.agame.common.entity;

import io.github.coffeecatrailway.orsomething.agame.client.texture.atlas.Atlases;
import io.github.coffeecatrailway.orsomething.anengine.client.BatchRenderer;
import io.github.coffeecatrailway.orsomething.anengine.client.texture.atlas.TextureAtlas;
import io.github.coffeecatrailway.orsomething.anengine.common.entity.Entity;
import io.github.coffeecatrailway.orsomething.anengine.common.entity.ai.PathFinderTask;
import io.github.coffeecatrailway.orsomething.anengine.common.entity.ai.Task;
import io.github.coffeecatrailway.orsomething.anengine.common.MatUtils;
import io.github.coffeecatrailway.orsomething.anengine.core.io.ObjectLocation;
import io.github.coffeecatrailway.orsomething.anengine.common.world.World;
import io.github.coffeecatrailway.orsomething.agame.core.registry.EntityRegistry;
import io.github.coffeecatrailway.orsomething.anengine.core.AnEngineOrSomething;
import org.joml.Math;
import org.joml.Vector2f;

import java.util.Random;

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
            case WANDER -> this.addTask(new WanderTask(this, 20f, 5f));
            case A_STAR -> this.addTask(this.pathFinderTask = new PathFinderTask(this, 20, 15f, 10f, 15f));
            case NONE -> {}
        }
    }

    @Override
    public void render(AnEngineOrSomething something, BatchRenderer batch)
    {
        super.render(something, batch, Atlases.ENTITY_ATLAS);
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

    /**
     * @author CoffeeCatRailway
     * Created: 07/01/2023
     */
    static class WanderTask extends Task
    {
        private final Vector2f destination = new Vector2f(0f);
        private final float wanderRadius, speed;

        public WanderTask(Entity entity, float wanderRadius, float speed)
        {
            super(entity);
            this.wanderRadius = wanderRadius;
            this.speed = speed;
            this.chooseDestination(entity.getWorld().random());
        }

        @Override
        public void tick(float delta, World world)
        {
            if (this.entity.getPosition().distance(this.destination) < .5f)
                this.chooseDestination(world.random());

            float x = this.destination.x - this.entity.getPosition().x;
            float y = this.destination.y - this.entity.getPosition().y;
            float dist = Math.sqrt(x * x + y * y);
            float mult = this.speed / dist;
            this.entity.getPosition().add(x * mult * delta, y * mult * delta);
        }

        private void chooseDestination(Random random)
        {
            float x = MatUtils.randomFloat(random, -this.wanderRadius, this.wanderRadius);
            float y = MatUtils.randomFloat(random, -this.wanderRadius, this.wanderRadius);
            this.destination.set(entity.getPosition()).add(x, y);
        }
    }
}
