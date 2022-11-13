package io.github.coffeecatrailway.agameorsomething.common.world;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;

/**
 * @author CoffeeCatRailway
 * Created: 12/11/2022
 */
public interface World
{
    void generate();

    void tick(AGameOrSomething something, Camera camera);

    void render(AGameOrSomething something, Camera camera);
}
