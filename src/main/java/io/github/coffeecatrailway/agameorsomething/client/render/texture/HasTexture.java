package io.github.coffeecatrailway.agameorsomething.client.render.texture;

import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;

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
