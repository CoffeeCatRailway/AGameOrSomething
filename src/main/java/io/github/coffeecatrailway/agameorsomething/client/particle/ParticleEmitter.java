package io.github.coffeecatrailway.agameorsomething.client.particle;

import io.github.coffeecatrailway.agameorsomething.client.render.BatchRenderer;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;

/**
 * @author CoffeeCatRailway
 * Created: 27/12/2022
 */
public interface ParticleEmitter
{
    void tick(float delta, AGameOrSomething something);

    void render(AGameOrSomething something, BatchRenderer batch);
}
