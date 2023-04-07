package io.github.coffeecatrailway.orsomething.anengine.client.graphics.texture;

/**
 * @author CoffeeCatRailway
 * Created: 06/04/2023
 */
public interface ITexture
{
    Texture getTexture();

    int getWidth();
    int getHeight();

    float getU0();
    float getV0();
    float getU1();
    float getV1();
}
