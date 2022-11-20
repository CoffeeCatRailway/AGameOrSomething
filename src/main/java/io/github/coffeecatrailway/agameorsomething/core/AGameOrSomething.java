package io.github.coffeecatrailway.agameorsomething.core;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.Shader;
import io.github.coffeecatrailway.agameorsomething.client.render.TileRenderer;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.TextureAtlas;
import io.github.coffeecatrailway.agameorsomething.client.render.vbo.VBOModels;
import io.github.ocelot.window.Window;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.common.world.TestWorld;
import io.github.coffeecatrailway.agameorsomething.common.world.World;
import io.github.coffeecatrailway.agameorsomething.core.registry.EntityRegistry;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import io.github.ocelot.window.WindowEventListener;
import io.github.ocelot.window.WindowManager;
import io.github.ocelot.window.input.KeyMods;
import io.github.ocelot.window.input.KeyboardHandler;
import io.github.ocelot.window.input.MouseHandler;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.slf4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author CoffeeCatRailway
 * Created: 13/07/2022
 */
public class AGameOrSomething implements WindowEventListener
{
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String NAMESPACE = "agos";

    public static final double FPS_CAP = 1d / 60d; // Used as delta

    private static AGameOrSomething INSTANCE;

    private final WindowManager windowManager;
    private final Window window;
    private final KeyboardHandler keyboardHandler;
    private final MouseHandler mouseHandler;
    private Camera camera;

    private TileRenderer tileRenderer;
    private World world;

    private AGameOrSomething(int width, int height, boolean fullscreen)
    {
        this.windowManager = new WindowManager();
        this.window = this.windowManager.create(width, height, false);
        this.window.setFullscreen(fullscreen);
        this.window.addListener(this);
        this.keyboardHandler = this.window.createKeyboardHandler();
        this.mouseHandler = this.window.createMouseHandler();
    }

    private void init()
    {
        // Configure
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

        // Create the window
        this.window.create("AGameOrSomething");
        GL.createCapabilities();

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

        this.camera = new Camera(this.window);

        TileRegistry.load();
        TextureAtlas.TILE_ATLAS.init();

        EntityRegistry.load();
        TextureAtlas.ENTITY_ATLAS.init();

        this.tileRenderer = new TileRenderer();
        this.world = new TestWorld();
    }

    private void loop()
    {
        glClearColor(0f, 0f, 0f, 0f); // Set clear color
        glEnable(GL_TEXTURE_2D);

        double fpsPassed = 0;
        int fps = 0;

        double timeLast = Timer.getTimeInSeconds();
        double unprocessedTime = 0;

        // Run until window is closed or 'ESCAPE' is pressed
        while (!this.window.isClosed())
        {
            this.windowManager.update();

            boolean shouldRender = false;

            double time = Timer.getTimeInSeconds();
            double timePassed = time - timeLast;
            unprocessedTime += timePassed;
            fpsPassed += timePassed;

            timeLast = time;

            while (unprocessedTime >= FPS_CAP) // Update logic (tick)
            {
                unprocessedTime -= FPS_CAP;
                shouldRender = true;

                this.camera.tick();
                this.world.tick((float) FPS_CAP, this, this.camera);

                if (fpsPassed >= 1d)
                {
                    fpsPassed = 0;
                    this.window.setTitle("AGameOrSomething - Fps: " + fps);
                    fps = 0;
                }
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            this.world.render(this, this.camera);
            fps++;
        }
    }

    private void destroy()
    {
        TextureAtlas.deleteStaticAtlases();
        Shader.deleteStaticShaders();
        VBOModels.deleteStaticModels();

        this.windowManager.free();
    }

    @Override
    public void framebufferResized(Window window, int width, int height) {
        this.camera.adjustProjection();
    }

    @Override
    public void keyPressed(Window window, int key, int scanCode, KeyMods mods) {
        if(key == GLFW_KEY_ESCAPE)
            this.window.setClosing(true);
    }

    public Window getWindow()
    {
        return this.window;
    }

    public KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }

    public MouseHandler getMouseHandler() {
        return mouseHandler;
    }

    public Camera getCamera()
    {
        return this.camera;
    }

    public TileRenderer getTileRenderer()
    {
        return this.tileRenderer;
    }

    public World getWorld()
    {
        return this.world;
    }

    public static AGameOrSomething getInstance()
    {
        return INSTANCE;
    }

    public static void main(String[] args)
    {
        LOGGER.info("LWJGL version: " + Version.getVersion());

        INSTANCE = new AGameOrSomething(800, 600, false);

        INSTANCE.init();
        INSTANCE.loop();
        INSTANCE.destroy();

        // Terminate GLFW & free
        LOGGER.warn("Terminating program");
    }
}
