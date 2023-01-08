package io.github.coffeecatrailway.agameorsomething.common.entity;

import io.github.coffeecatrailway.agameorsomething.client.render.BatchRenderer;
import io.github.coffeecatrailway.agameorsomething.client.render.LineRenderer;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.atlas.TextureAtlas;
import io.github.coffeecatrailway.agameorsomething.common.entity.ai.PathFinderTask;
import io.github.coffeecatrailway.agameorsomething.common.entity.ai.SimpleWanderTask;
import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.EntityRegistry;
import org.joml.Vector2ic;

/**
 * @author CoffeeCatRailway
 * Created: 28/11/2022
 */
public class TestEntity extends Entity
{
    private boolean shouldWander = true;
    private boolean aStar = false;

    public PathFinderTask pathFinderTask;

    public TestEntity(boolean shouldWander, boolean aStar)
    {
        this(EntityRegistry.TEST.get().entityData);
        this.setId(EntityRegistry.TEST.get());
        this.shouldWander = shouldWander;
        this.aStar = aStar;
    }

    public TestEntity(EntityData entityData)
    {
        super(entityData);
    }

    @Override
    public void init()
    {
        if (this.shouldWander)
            this.addTask(new SimpleWanderTask(this, 20f, 5f));
        if (this.aStar)
            this.addTask(this.pathFinderTask = new PathFinderTask(this, 20));
    }

    @Override
    public void render(AGameOrSomething something, BatchRenderer batch)
    {
        super.render(something, batch);
        if (AGameOrSomething.isDebugRender() && this.pathFinderTask != null && this.pathFinderTask.path.size() > 1)
        {
            float[] verticies = new float[this.pathFinderTask.path.size() * 2];
            int i = 0;
            for (Vector2ic spot : this.pathFinderTask.path)
            {
                verticies[i] = spot.x() + .5f;
                verticies[i + 1] = spot.y() + .5f;
                i += 2;
            }
            LineRenderer.setLineColor(1f, 1f, 0f);
            LineRenderer.drawLineStrip(verticies);
        }
    }

    @Override
    public ObjectLocation getTextureLocation()
    {
        return TextureAtlas.MISSING;
    }
}
