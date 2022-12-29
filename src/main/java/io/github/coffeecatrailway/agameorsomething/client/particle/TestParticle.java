package io.github.coffeecatrailway.agameorsomething.client.particle;

import io.github.coffeecatrailway.agameorsomething.client.render.texture.atlas.TextureAtlas;
import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * @author CoffeeCatRailway
 * Created: 27/12/2022
 */
public class TestParticle implements Particle
{
    private float time = 0f;

    private final Vector2f position = new Vector2f(0f);
    private final Vector2f velocity = new Vector2f(0f);

    private float lifespan;

    private boolean spin = false;

    public TestParticle(Vector2fc position, Vector2fc velocity, float lifespan)
    {
        this.position.set(position);
        this.velocity.set(velocity);
        this.lifespan = lifespan;
    }

    public TestParticle spin()
    {
        this.spin = true;
        return this;
    }

    public void tick(float delta)
    {
        if (this.spin)
        {
            this.time += .01f;

            float i = 10f;
            float j = 10f;
//            float i = this.velocity.x;
//            float j = this.velocity.y;
            this.position.x += Math.sin(this.time * i) * j * delta;
            this.position.y += Math.cos(this.time * i) * j * delta;
        } else
            this.position.add(this.velocity.x * delta, this.velocity.y * delta);
        this.lifespan -= delta;
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
    public ObjectLocation getTextureLocation()
    {
        return TextureAtlas.MISSING;
    }
}
