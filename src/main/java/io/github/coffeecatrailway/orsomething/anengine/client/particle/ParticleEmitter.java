package io.github.coffeecatrailway.orsomething.anengine.client.particle;

import io.github.coffeecatrailway.orsomething.anengine.client.graphics.BatchRenderer;
import io.github.coffeecatrailway.orsomething.anengine.core.AnEngineOrSomething;

/**
 * @author CoffeeCatRailway
 * Created: 27/12/2022
 */
public interface ParticleEmitter
{
    void tick(float delta, AnEngineOrSomething something);

    void render(AnEngineOrSomething something, BatchRenderer batch);
}
