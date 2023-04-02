package io.github.coffeecatrailway.orsomething.anengine.client.texture;

import io.github.coffeecatrailway.orsomething.anengine.core.io.ObjectLocation;

/**
 * @author CoffeeCatRailway
 * Created: 16/11/2022
 */
public interface HasTexture
{
    default boolean hasTexture()
    {
        return true;
    }

    ObjectLocation getTextureLocation();
}
