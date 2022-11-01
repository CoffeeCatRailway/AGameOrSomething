package io.github.coffeecatrailway.agameorsomething.common.io;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFWCursorPosCallback;

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

    private static final Vector2d MOUSE_POSITION = new Vector2d();

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

    public void setMousePosCallback()
    {
        GLFWCursorPosCallback callback = new GLFWCursorPosCallback()
        {
            @Override
            public void invoke(long window, double xPos, double yPos)
            {
                if (window != InputHandler.this.window.getId())
                    return;
                MOUSE_POSITION.set(xPos - InputHandler.this.window.getWidth() / 2d, -(yPos - InputHandler.this.window.getHeight() / 2d));
            }
        };
        glfwSetCursorPosCallback(this.window.getId(), callback);
    }

    public static Vector2d getMousePosition()
    {
        return MOUSE_POSITION;
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
