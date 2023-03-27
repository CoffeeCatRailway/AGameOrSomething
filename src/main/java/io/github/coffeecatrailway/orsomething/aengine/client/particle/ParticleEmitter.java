package io.github.coffeecatrailway.orsomething.aengine.client.particle;

import io.github.coffeecatrailway.orsomething.aengine.client.BatchRenderer;
import io.github.coffeecatrailway.orsomething.agame.common.AGameOrSomething;

/**
 * @author CoffeeCatRailway
 * Created: 27/12/2022
 */
public interface ParticleEmitter
{
    void tick(float delta, AGameOrSomething something);

    void render(AGameOrSomething something, BatchRenderer batch);
}
