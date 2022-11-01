package io.github.coffeecatrailway.agameorsomething.core;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.Shader;
import io.github.coffeecatrailway.agameorsomething.client.render.Texture;
import io.github.coffeecatrailway.agameorsomething.client.render.TileRenderer;
import io.github.coffeecatrailway.agameorsomething.common.io.Window;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.common.world.World;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLUtil;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author CoffeeCatRailway
 * Created: 13/07/2022
 */
public class AGameOrSomething
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String NAMESPACE = "agos";

    private static AGameOrSomething INSTANCE;

    private final Window window;
    private Camera camera;
    private TileRenderer tileRenderer;
    private World world;

    private AGameOrSomething(int width, int height, boolean fullscreen)
    {
        this.window = new Window(width, height).setFullscreen(fullscreen);
    }

    private void init()
    {
        // Setup error callback. Prints error to System.err
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        // Initialize GLFW
        if (!glfwInit())
        {
            LOGGER.error("Unable to initialize GLFW!");
            throw new IllegalStateException("Unable to initialize GLFW!");
        }

        // Configure
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        this.window.initialize("AGameOrSomething");

        GLUtil.setupDebugMessageCallback(new PrintStream(new OutputStream()
        {
            private StringBuffer buffer = new StringBuffer();

            @Override
            public void write(final int b)
            {
                if ((char) b == '\n')
                {
                    flush();
                    return;
                }
                this.buffer.append((char) b);
            }

            @Override
            public void flush()
            {
                LOGGER.debug(this.buffer.toString());
                this.buffer = new StringBuffer();
            }
        }));

        this.camera = new Camera(this.window.getWidth(), this.window.getHeight());

        TileRegistry.load();
        this.tileRenderer = new TileRenderer();

        this.world = new World();
    }

    private void loop()
    {
        glClearColor(0f, 0f, 0f, 0f); // Set clear color
        glEnable(GL_TEXTURE_2D);

        double fpsCap = 1d / 60d;
        double fpsPassed = 0;
        int fps = 0;

        double timeLast = Timer.getTimeInSeconds();
        double unprocessedTime = 0;

        float sped = 1f / 16f;

        // Run until window is closed or 'ESCAPE' is pressed
        while (!this.window.shouldClose())
        {
            boolean shouldRender = false;

            double time = Timer.getTimeInSeconds();
            double timePassed = time - timeLast;
            unprocessedTime += timePassed;
            fpsPassed += timePassed;

            timeLast = time;

            while (unprocessedTime >= fpsCap) // Update logic (tick)
            {
                if (this.window.hasResized())
                {
                    this.camera.setProjection(this.window.getWidth(), this.window.getHeight());
                    //gui & world stuffs
                    glViewport(0, 0, this.window.getWidth(), this.window.getHeight());
                }

                unprocessedTime -= fpsCap;
                shouldRender = true;

                if (Window.getInputHandler().isKeyPressed(GLFW_KEY_ESCAPE))
                    glfwSetWindowShouldClose(this.window.getId(), true);

                if (Window.getInputHandler().isKeyDown(GLFW_KEY_A))
                    this.camera.addPosition(new Vector3f(sped, 0f, 0f));
                if (Window.getInputHandler().isKeyDown(GLFW_KEY_D))
                    this.camera.addPosition(new Vector3f(-sped, 0f, 0f));

                if (Window.getInputHandler().isKeyDown(GLFW_KEY_W))
                    this.camera.addPosition(new Vector3f(0f, -sped, 0f));
                if (Window.getInputHandler().isKeyDown(GLFW_KEY_S))
                    this.camera.addPosition(new Vector3f(0f, sped, 0f));

                if (Window.getInputHandler().isKeyDown(GLFW_KEY_UP))
                    this.camera.zoom(sped);
                if (Window.getInputHandler().isKeyDown(GLFW_KEY_DOWN))
                    this.camera.zoom(-sped);

                if (Window.getInputHandler().isKeyDown(GLFW_KEY_C))
                    this.camera.setPosition(new Vector2f(0f, 0f));

                this.world.tick(this);

                this.window.tick();

                if (fpsPassed >= 1d)
                {
                    fpsPassed = 0;
                    this.window.setTitleSuffix("Fps: " + fps);
                    fps = 0;
                }
            }

            if (shouldRender)
            {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

                this.world.render(this, this.camera);

                glfwSwapBuffers(this.window.getId()); // swap the color buffers
                fps++;
            }
        }
    }

    private void destroy()
    {
        this.tileRenderer.delete();
        this.world.destroy();

        Texture.MISSING.delete();
        Shader.deleteStaticShaders();

        this.window.destroy();
    }

    public Window getWindow()
    {
        return this.window;
    }

    public Camera getCamera()
    {
        return this.camera;
    }

    public TileRenderer getTileRenderer()
    {
        return this.tileRenderer;
    }

    public static AGameOrSomething getInstance()
    {
        return INSTANCE;
    }

    public static void main(String[] args)
    {
        LOGGER.info("LWJGL version: " + Version.getVersion());

        INSTANCE = new AGameOrSomething(600, 600, false);

        INSTANCE.init();
        INSTANCE.loop();
        INSTANCE.destroy();

        // Terminate GLFW & free
        LOGGER.warn("Terminating program");
        glfwTerminate();
        GLFWErrorCallback prevCallback = glfwSetErrorCallback(null);
        if (prevCallback != null)
            prevCallback.free();
    }
}
