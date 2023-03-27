package io.github.coffeecatrailway.orsomething.aengine.client.texture;

import io.github.coffeecatrailway.orsomething.aengine.ObjectLocation;

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
