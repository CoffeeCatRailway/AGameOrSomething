package io.github.coffeecatrailway.agameorsomething.client.particle;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.agameorsomething.client.render.BatchRenderer;
import io.github.coffeecatrailway.agameorsomething.client.render.LineRenderer;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.atlas.TextureAtlas;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
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
    public void tick(float delta, AGameOrSomething something)
    {
        Timer.start("particleTicking"); //TODO: Optimize
        if (this.particles.size() < this.maxParticles)
            this.particles.add(this.factory.create(this.position));

        this.particles.forEach(particle -> particle.tick(delta));
        this.particles.removeIf(particle -> particle.getLifespan() <= 0f);
        long millis = Timer.end("particleTicking");
        if (millis >= 30L)
            LOGGER.warn("Particle ticking took {}ms", millis);
    }

    @Override
    public void render(AGameOrSomething something, BatchRenderer batch)
    {
        Timer.start("particleRendering"); //TODO: Optimize
        batch.begin();
        batch.setColor(1f, 1f, 1f, .75f);

        this.particles.forEach((particle) -> batch.draw(TextureAtlas.PARTICLE_ATLAS.getEntry(particle.getTextureLocation()), particle.getPosition().x() - particle.getSize() / 2f, particle.getPosition().y() - particle.getSize() / 2f, particle.getSize(), particle.getSize()));

        batch.end();
        long millis = Timer.end("particleRendering");
        if (millis >= 30L)
            LOGGER.warn("Particle rendering took {}ms", millis);

        if (AGameOrSomething.isDebugRender())
        {
            LineRenderer.setLineColor(1f, 0f, 1f);
            LineRenderer.drawBox(this.position.x - .5f, this.position.y - .5f, this.position.x + .5f, this.position.y + .5f);
        }
    }

    public interface Factory
    {
        Particle create(Vector2fc origin);
    }
}
