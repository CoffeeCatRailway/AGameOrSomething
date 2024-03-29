package io.github.coffeecatrailway.orsomething.agame.client.particle;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.orsomething.agame.client.texture.atlas.Atlases;
import io.github.coffeecatrailway.orsomething.anengine.client.graphics.BatchRenderer;
import io.github.coffeecatrailway.orsomething.anengine.client.graphics.LineRenderer;
import io.github.coffeecatrailway.orsomething.anengine.client.particle.Particle;
import io.github.coffeecatrailway.orsomething.anengine.client.particle.ParticleEmitter;
import io.github.coffeecatrailway.orsomething.anengine.common.Timer;
import io.github.coffeecatrailway.orsomething.agame.core.AGameOrSomething;
import io.github.coffeecatrailway.orsomething.anengine.core.AnEngineOrSomething;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * @author CoffeeCatRailway
 * Created: 27/12/2022
 */
public class SimpleParticleEmitter implements ParticleEmitter
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Set<Particle> particles = new HashSet<>();

    private final Vector2f position = new Vector2f(0f);
    private final int maxParticles;

    private final Factory factory;

    public SimpleParticleEmitter(Vector2fc position, int maxParticles, Factory factory)
    {
        this.position.set(position);
        this.maxParticles = maxParticles;
        this.factory = factory;
    }

    @Override
    public void tick(float delta, AnEngineOrSomething something)
    {
        if (!something.getCamera().getCullingFilter().isInside(this.position))
            return;
        Timer.start("particleTicking");
        if (this.particles.size() < this.maxParticles)
            for (int i = 0; i < this.maxParticles - this.particles.size(); i++)
                this.particles.add(this.factory.create(this.position));

        this.particles.forEach(particle -> particle.tick(delta));
        this.particles.removeIf(particle -> particle.getLifespan() <= 0f);
        long millis = Timer.end("particleTicking");
        if (millis >= 30L)
            LOGGER.warn("Particle ticking took {}ms", millis);
    }

    @Override
    public void render(AnEngineOrSomething something, BatchRenderer batch)
    {
        if (!something.getCamera().getCullingFilter().isInside(this.position))
            return;
        Timer.start("particleRendering");
        batch.begin();
        batch.setColor(0.8745098f, 0.8784314f, 0.9098039f, .75f);

        this.particles.stream().filter(particle -> something.getCamera().getCullingFilter().isInside(particle.getPosition())).forEach(particle -> batch.draw(Atlases.PARTICLE_ATLAS.getEntry(particle.getTextureLocation()), particle.getPosition().x() - particle.getSize() / 2f, particle.getPosition().y() - particle.getSize() / 2f, particle.getSize(), particle.getSize()));

        batch.end();
        long millis = Timer.end("particleRendering");
        if (millis >= 30L)
            LOGGER.warn("Particle rendering took {}ms", millis);

        if (AGameOrSomething.DEBUG_RENDER.get())
        {
            LineRenderer.INSTANCE.begin(1f, 0f, 1f);
            LineRenderer.INSTANCE.drawBox(this.position.x - .5f, this.position.y - .5f, this.position.x + .5f, this.position.y + .5f);
            LineRenderer.INSTANCE.end();
        }
    }

    public interface Factory
    {
        Particle create(Vector2fc origin);
    }
}
