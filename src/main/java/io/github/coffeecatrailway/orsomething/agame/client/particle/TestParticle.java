package io.github.coffeecatrailway.orsomething.agame.client.particle;

import io.github.coffeecatrailway.orsomething.aengine.MatUtils;
import io.github.coffeecatrailway.orsomething.aengine.ObjectLocation;
import io.github.coffeecatrailway.orsomething.aengine.client.particle.Particle;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * @author CoffeeCatRailway
 * Created: 27/12/2022
 */
public class TestParticle implements Particle
{
    private static final ObjectLocation SMOKE_0 = new ObjectLocation("smoke0");
    private static final ObjectLocation SMOKE_1 = new ObjectLocation("smoke1");

    private final Vector2f position = new Vector2f(0f);
    private final Vector2f velocity = new Vector2f(0f);

    private final float size;
    private float lifespan;
    private boolean spin = false;

    public TestParticle(Vector2fc position, Vector2fc velocity, float lifespan)
    {
        this.position.set(position);
        this.velocity.set(velocity);
        this.lifespan = lifespan;
        this.size = MatUtils.randomFloat(.4f, .6f);
    }

    public TestParticle spin()
    {
        this.spin = true;
        return this;
    }

    public void tick(float delta)
    {
        if (this.spin)
            this.position.set(this.rotateAround(this.position, this.velocity, this.lifespan * 4f));
        else
            this.position.add(this.velocity.x * delta, this.velocity.y * delta);
        this.lifespan -= delta;
    }

    private Vector2f rotateAround(Vector2fc point, Vector2fc origin, float angle)
    {
        float radians = Math.toRadians(angle);
        float sin = Math.sin(radians);
        float cos = Math.cos(radians);

        Vector2f ret = point.sub(origin, new Vector2f());
        return ret.set(ret.x * cos + ret.y * sin, -ret.x * sin + ret.y * cos).add(origin);
    }

    @Override
    public Vector2f getPosition()
    {
        return this.position;
    }

    @Override
    public float getLifespan()
    {
        return this.lifespan;
    }

    @Override
    public float getSize()
    {
        return this.size;
    }

    @Override
    public ObjectLocation getTextureLocation()
    {
        return this.size >= .5f ? SMOKE_0 : SMOKE_1;
    }
}
