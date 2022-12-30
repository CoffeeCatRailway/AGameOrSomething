package io.github.coffeecatrailway.agameorsomething.client.particle;

import io.github.coffeecatrailway.agameorsomething.client.render.texture.HasTexture;
import org.joml.Vector2f;

/**
 * @author CoffeeCatRailway
 * Created: 27/12/2022
 */
public interface Particle extends HasTexture
{
    void tick(float delta);

    Vector2f getPosition();

    float getLifespan();

    float getSize();
}
