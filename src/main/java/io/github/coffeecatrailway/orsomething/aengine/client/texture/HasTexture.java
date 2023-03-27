package io.github.coffeecatrailway.orsomething.aengine.client.texture;

import io.github.coffeecatrailway.orsomething.aengine.ObjectLocation;

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
