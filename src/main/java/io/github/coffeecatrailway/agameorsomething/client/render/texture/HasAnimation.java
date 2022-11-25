package io.github.coffeecatrailway.agameorsomething.client.render.texture;

import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;

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
