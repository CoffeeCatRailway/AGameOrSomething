package io.github.coffeecatrailway.orsomething.anengine.client.texture;

import io.github.coffeecatrailway.orsomething.anengine.core.io.ObjectLocation;

/**
 * @author CoffeeCatRailway
 * Created: 25/11/2022
 */
public interface HasAnimation extends HasTexture
{
    Animation[] getAnimations();

    @Override
    default ObjectLocation getTextureLocation()
    {
        return this.getAnimations()[0].getCurrentFrame();
    }
}
