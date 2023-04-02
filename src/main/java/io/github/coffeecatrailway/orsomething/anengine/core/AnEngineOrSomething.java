package io.github.coffeecatrailway.orsomething.anengine.core;

import io.github.coffeecatrailway.orsomething.anengine.client.camera.Camera;
import io.github.coffeecatrailway.orsomething.anengine.common.world.World;
import io.github.ocelot.window.Window;
import io.github.ocelot.window.input.KeyboardHandler;
import io.github.ocelot.window.input.MouseHandler;

/**
 * @author CoffeeCatRailway
 * Created: 27/03/2023
 */
public interface AnEngineOrSomething
{
    void init();

    void run();

    void destroy();

    Window getWindow();

    KeyboardHandler getKeyboardHandler();

    MouseHandler getMouseHandler();

    Camera getCamera();

    World getWorld();
}
