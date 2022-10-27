package io.github.coffeecatrailway.agameorsomething.common.io;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author CoffeeCatRailway
 * Created: 19/07/2022
 */
public class InputHandler
{
    private final Window window;
    private final boolean[] keys;
    private final boolean[] mouseButtons;

    private static final Vector2f MOUSE_POS = new Vector2f();
    private static final double[] MX = new double[1], MY = new double[1];
    private static final int[] WIDTH = new int[1], HEIGHT = new int[1];

    public InputHandler(Window window)
    {
        this.window = window;
        this.keys = new boolean[GLFW_KEY_LAST];
        this.mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];

        for (int i = 32; i < GLFW_KEY_LAST; i++)
            this.keys[i] = false;
        for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++)
            this.mouseButtons[i] = false;
    }

    public boolean isKeyDown(int key)
    {
        return glfwGetKey(this.window.getId(), key) == GLFW_PRESS;
    }

    public boolean isKeyPressed(int key)
    {
        return (this.isKeyDown(key) && !this.keys[key]);
    }

    public boolean isKeyReleased(int key)
    {
        return (!this.isKeyDown(key) && this.keys[key]);
    }

    public boolean isMouseButtonDown(int button)
    {
        return glfwGetMouseButton(this.window.getId(), button) == GLFW_PRESS;
    }

    public boolean isMouseButtonPressed(int button)
    {
        return (this.isMouseButtonDown(button) && !this.mouseButtons[button]);
    }

    public boolean isMouseButtonReleased(int button)
    {
        return (!this.isMouseButtonDown(button) && this.mouseButtons[button]);
    }

    public Vector2f getMousePosition()
    {
        glfwGetCursorPos(this.window.getId(), MX, MY);
        glfwGetWindowSize(this.window.getId(), WIDTH, HEIGHT);

        MOUSE_POS.set((float) MX[0] - (WIDTH[0] / 2d), -((float) MY[0] - (HEIGHT[0] / 2d)));
        return MOUSE_POS;
    }

    public void tick()
    {
        int i;
        for (i = 32; i < GLFW_KEY_LAST; i++)
            this.keys[i] = this.isKeyDown(i);
        for (i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++)
            this.mouseButtons[i] = this.isMouseButtonDown(i);
    }
}
