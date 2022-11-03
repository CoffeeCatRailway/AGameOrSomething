package io.github.coffeecatrailway.agameorsomething.common.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2i;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author CoffeeCatRailway
 * Created: 18/07/2022
 */
public class Window
{
    public static final Logger LOGGER = LogManager.getLogger();

    private long id;

    private int width, height;
    private String baseTitle = "";
    private Vector2i position;

    private boolean fullscreen;
    private boolean hasResized;

    private static InputHandler inputHandler;

    public Window(int width, int height)
    {
        this.width = width;
        this.height = height;

        this.fullscreen = false;
        this.hasResized = false;
    }

    public void initialize(String baseTitle)
    {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        this.baseTitle = baseTitle;
        this.id = glfwCreateWindow(this.width, this.height, this.baseTitle, this.fullscreen ? glfwGetPrimaryMonitor() : 0, 0);
        if (this.id == 0)
            throw new RuntimeException("Failed to create GLFW window!");

        if (!this.fullscreen)
        {
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                glfwGetWindowSize(this.id, width, height);

                // Get resolution of the primary monitor & center window
                GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
                glfwSetWindowPos(this.id, (vidMode.width() - width.get(0)) / 2, (vidMode.height() - height.get(0)) / 2);
                LOGGER.info("Set size & position");
            }
        }

        glfwMakeContextCurrent(this.id); // Make OpenGL context current
        GL.createCapabilities();
        glfwSwapInterval(0); // V-Sync
        glfwShowWindow(this.id); // Make visible

        inputHandler = new InputHandler(this);
        this.setCallbacks();
        LOGGER.info("Initialized");
    }

    private void setCallbacks()
    {
        GLFWWindowSizeCallback sizeCallback = new GLFWWindowSizeCallback()
        {
            @Override
            public void invoke(long window, int width, int height)
            {
                if (window != Window.this.id)
                    return;
                Window.this.width = width;
                Window.this.height = height;
                Window.this.hasResized = true;
                LOGGER.debug("Window resized to {}x{}", width, height);
            }
        };
        glfwSetWindowSizeCallback(this.id, sizeCallback);
        GLFWWindowPosCallback posCallback = new GLFWWindowPosCallback()
        {
            @Override
            public void invoke(long window, int xPos, int yPos)
            {
                if (window != Window.this.id)
                    return;
                Window.this.position = new Vector2i(xPos, yPos);
                LOGGER.debug("Window moved to {}x{}", xPos, yPos);
            }
        };
        glfwSetWindowPosCallback(this.id, posCallback);
        inputHandler.setMousePosCallback();;
    }

    public void destroy()
    {
        Callbacks.glfwFreeCallbacks(this.id);
        glfwDestroyWindow(this.id);
        LOGGER.debug("Destroyed window {}", this.id);
    }

    public boolean shouldClose()
    {
        return glfwWindowShouldClose(this.id);
    }

    public void tick()
    {
        this.hasResized = false;
        inputHandler.tick();
        glfwPollEvents();
    }

    public void setTitleSuffix(CharSequence suffix)
    {
        if (suffix.isEmpty())
            glfwSetWindowTitle(this.id, this.baseTitle);
        else
            glfwSetWindowTitle(this.id, this.baseTitle + " - " + suffix);
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public Vector2i getPosition()
    {
        return this.position;
    }

    public boolean isFullscreen()
    {
        return this.fullscreen;
    }

    public Window setFullscreen(boolean fullscreen)
    {
        this.fullscreen = fullscreen;
        return this;
    }

    public boolean hasResized()
    {
        return this.hasResized;
    }

    public long getId()
    {
        return this.id;
    }

    public static InputHandler getInputHandler()
    {
        return inputHandler;
    }
}
