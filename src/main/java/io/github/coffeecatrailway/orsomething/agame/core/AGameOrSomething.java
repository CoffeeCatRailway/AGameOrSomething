package io.github.coffeecatrailway.orsomething.agame.core;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.orsomething.agame.client.texture.atlas.Atlases;
import io.github.coffeecatrailway.orsomething.agame.common.world.TestWorld;
import io.github.coffeecatrailway.orsomething.agame.core.registry.AGameTiles;
import io.github.coffeecatrailway.orsomething.agame.core.registry.EntityRegistry;
import io.github.coffeecatrailway.orsomething.anengine.client.camera.Camera;
import io.github.coffeecatrailway.orsomething.anengine.client.graphics.BatchRenderer;
import io.github.coffeecatrailway.orsomething.anengine.client.graphics.FBO;
import io.github.coffeecatrailway.orsomething.anengine.client.graphics.LineRenderer;
import io.github.coffeecatrailway.orsomething.anengine.client.graphics.shader.Shader;
import io.github.coffeecatrailway.orsomething.anengine.client.graphics.texture.atlas.TextureAtlas;
import io.github.coffeecatrailway.orsomething.anengine.common.MatUtils;
import io.github.coffeecatrailway.orsomething.anengine.common.Timer;
import io.github.coffeecatrailway.orsomething.anengine.common.world.World;
import io.github.coffeecatrailway.orsomething.anengine.core.AnEngineOrSomething;
import io.github.ocelot.window.Window;
import io.github.ocelot.window.WindowEventListener;
import io.github.ocelot.window.WindowManager;
import io.github.ocelot.window.input.KeyMods;
import io.github.ocelot.window.input.KeyboardHandler;
import io.github.ocelot.window.input.MouseHandler;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.slf4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author CoffeeCatRailway
 * Created: 13/07/2022
 */
public class AGameOrSomething implements AnEngineOrSomething, WindowEventListener
{
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final double FPS_CAP = 1d / 60d;

    private static boolean RENDER_UNFOCUSED = true;
    private static AGameOrSomething INSTANCE;

    private final WindowManager windowManager;
    private final Window window;
    private final KeyboardHandler keyboardHandler;
    private final MouseHandler mouseHandler;
    private Camera camera;

    private World world;

    private AGameOrSomething(int width, int height, boolean fullscreen)
    {
        this.windowManager = new WindowManager();
        this.window = this.windowManager.create(width, height, fullscreen);
        this.window.setVsync(false);
        this.window.setFullscreen(fullscreen);
        this.window.addListener(this);
        this.keyboardHandler = this.window.createKeyboardHandler();
        this.mouseHandler = this.window.createMouseHandler();
    }

    @Override
    public void init()
    {
        // Configure
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
//        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
//        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
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
                    this.flush();
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

        // Initialize game objects & registries
        this.camera = new Camera(this.window);

        AGameTiles.TILES.load();
        Atlases.TILE_ATLAS.init();

        EntityRegistry.ENTITIES.load();
        Atlases.ENTITY_ATLAS.init();

        Atlases.PARTICLE_ATLAS.init();

        this.world = new TestWorld();
    }

    Shader lightShader;
    @Override
    public void run()
    {
        double fpsPassed = 0;
        int fps = 0;
        long frameTime = 0;

        double timeLast = Timer.getTimeInSeconds();
        double deltaTime = 0;

        BatchRenderer batch = new BatchRenderer();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glClearColor(0f, 0f, 0f, 0f);

//        VAO vao = new VAO(6, new ShaderAttribute("position", 3, GL_FLOAT));
//        vao.bind();
//        vao.bindWrite();
//        vao.put(0, buffer -> buffer.putFloat(-1f).putFloat(1f).putFloat(0f));
//        vao.put(1, buffer -> buffer.putFloat(1f).putFloat(1f).putFloat(0f));
//        vao.put(2, buffer -> buffer.putFloat(-1f).putFloat(-1f).putFloat(0f));
//        vao.put(3, buffer -> buffer.putFloat(1f).putFloat(1f).putFloat(0f));
//        vao.put(4, buffer -> buffer.putFloat(1f).putFloat(-1f).putFloat(0f));
//        vao.put(5, buffer -> buffer.putFloat(-1f).putFloat(-1f).putFloat(0f));
//        vao.unbind();
        FBO fbo = new FBO(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());

        lightShader = new Shader("light", "light");
        lightShader.bind();
        float ambient = .1f;
        lightShader.setUniformVector4f("uAmbient", ambient, ambient, ambient, 1f);
        lightShader.setUniformVector2f("uResolution", this.window.getFramebufferWidth(), this.window.getFramebufferHeight());

        lightShader.setUniformVector2f("uBoxes[0].start", .45f, .45f);
        lightShader.setUniformVector2f("uBoxes[0].end", .55f, .55f);
        lightShader.setUniformVector2f("uBoxes[1].start", .1f, .1f);
        lightShader.setUniformVector2f("uBoxes[1].end", .2f, .9f);
        lightShader.unbind();

        final class Light
                {
                    private final Vector2f position = new Vector2f();
                    private final Vector3f color = new Vector3f(1f);
                    private float min;
                    private float max;
                    private float brightness;

                    Light(Vector2fc position, Vector3fc color, float min, float max, float brightness)
                    {
                        this.position.set(position);
                        this.color.set(color);
                        this.min = min;
                        this.max = max;
                        this.brightness = Math.clamp(0f, 1f, brightness);
                    }
                }
        List<Light> lights = new ArrayList<>();
        Light redLight;

        lights.add(redLight = new Light(new Vector2f(0.875f, 0.7777778f), new Vector3f(1f, 0f, 0f), 0f, 1f, 1f));
        lights.add(new Light(new Vector2f(0.875f, 0.22222222f), new Vector3f(1f), 0f, .75f, .75f));

        // Run until window is closed or 'ESCAPE' is pressed
        while (!this.window.isClosed())
        {
            this.windowManager.update();

            double time = Timer.getTimeInSeconds();
            double timePassed = time - timeLast;
            deltaTime += timePassed;
            fpsPassed += timePassed;

            timeLast = time;

            while (deltaTime >= FPS_CAP) // Update logic (tick)
            {
                Timer.start("gameTick");
                this.tick((float) deltaTime, this);

                float dx = .5f * (Math.sin((float) glfwGetTime()) * .5f + .5f);
                redLight.position.x = 0.875f - dx;

                Vector2f mousePos = new Vector2f((float) this.mouseHandler.getMouseX(), (float) (-this.mouseHandler.getMouseY() + this.window.getWindowHeight())).div(AGameOrSomething.this.window.getWindowWidth(), AGameOrSomething.this.window.getWindowHeight());
                if (this.mouseHandler.isButtonPressed(GLFW_MOUSE_BUTTON_1) && lights.stream().map(light1 -> light1.position).noneMatch(position -> position.distance(mousePos) <= .05f))
                    lights.add(new Light(mousePos, new Vector3f(MatUtils.randomFloat(.1f, 1f), MatUtils.randomFloat(.1f, 1f), MatUtils.randomFloat(.1f, 1f)),
                            0f, MatUtils.randomFloat(.1f, 1f), MatUtils.randomFloat(.2f, 1f)));

                if (fpsPassed >= 1d)
                {
                    fpsPassed = 0d;
                    this.window.setTitle("AGameOrSomething - Fps: " + fps + ", " + frameTime + "ms");
                    fps = 0;
                }

                deltaTime = 0d;
                long millis = Timer.end("gameTick");
                if (millis >= 30L)
                    LOGGER.warn("A single game tick took {}ms", millis);
            }

            frameTime = System.currentTimeMillis();
            if (this.window.isFocused() || RENDER_UNFOCUSED)
            {
                batch.setShader(BatchRenderer.SHADER, false, this.camera);
                fbo.begin();
                this.render(this, batch);
                fbo.end(this.window);

                // Render code
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

//                lightShader.bind();
                batch.setShader(lightShader, false, this.camera);
                batch.begin();

                Atlases.TILE_ATLAS.getAtlasTexture().bind(true, 0);
                lightShader.setUniformi("uTexture", 0);

//                lightShader.setUniformMatrix4f("uProjection", this.camera.getProjectionMatrix());
//                lightShader.setUniformMatrix4f("uView", this.camera.getViewMatrix());

                for (int i = 0; i < lights.size(); i++)
                {
                    Light l = lights.get(i);
                    lightShader.setUniformVector2f("uLights[" + i + "].position", l.position.x, l.position.y);
                    lightShader.setUniformVector3f("uLights[" + i + "].color", l.color.x, l.color.y, l.color.z);
                    lightShader.setUniformf("uLights[" + i + "].min", l.min);
                    lightShader.setUniformf("uLights[" + i + "].max", l.max);
                    lightShader.setUniformf("uLights[" + i + "].brightness", l.brightness);
                }

//                glBlendFunc(GL_ONE, GL_ONE);
//                vao.bind();
//                vao.draw(GL_TRIANGLES);
//                lightShader.unbind();
                batch.draw(fbo.getTexture(), -1f, -1f, 2f, 2f, 0f, 0f, 1f, 1f);
                batch.end();
				
                fps++;
            }
            frameTime = System.currentTimeMillis() - frameTime;
        }
    }

    private void tick(float deltaTime, AGameOrSomething something)
    {
        // Debug keys code
        if (this.keyboardHandler.isKeyPressed(GLFW_KEY_F1))
            DEBUG_RENDER.set(!DEBUG_RENDER.get());

        this.camera.tick(this.keyboardHandler);
        this.world.tick(deltaTime, something);
    }

    private void render(AGameOrSomething something, BatchRenderer batch)
    {
        // Render code
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Update batch renderer uniforms
        batch.updateUniforms(this.camera);
        batch.setColor(0f, 0f, 0f, 1f);

        // Update debug uniforms
        if (DEBUG_RENDER.get())
            LineRenderer.INSTANCE.updateUniforms(something.getCamera());

        this.world.render(something, batch);
    }

    @Override
    public void destroy()
    {
        Atlases.delete();
        TextureAtlas.deleteStatic();
        BatchRenderer.SHADER.delete();
        LineRenderer.SHADER.delete();
        lightShader.delete();

        this.windowManager.free();
    }

    @Override
    public void framebufferResized(Window window, int width, int height)
    {
        this.camera.adjustProjection();
        lightShader.bind();
        lightShader.setUniformVector2f("uResolution", width, height);
    }

    @Override
    public void keyPressed(Window window, int key, int scanCode, KeyMods mods)
    {
        if (key == GLFW_KEY_ESCAPE)
            this.window.setClosing(true);
    }

    @Override
    public Window getWindow()
    {
        return this.window;
    }

    @Override
    public KeyboardHandler getKeyboardHandler()
    {
        return keyboardHandler;
    }

    @Override
    public MouseHandler getMouseHandler()
    {
        return mouseHandler;
    }

    @Override
    public Camera getCamera()
    {
        return this.camera;
    }

    @Override
    public World getWorld()
    {
        return this.world;
    }

//    public static AGameOrSomething getInstance()
//    {
//        return INSTANCE;
//    }

    public static void main(String[] args)
    {
        LOGGER.info("LWJGL version: " + Version.getVersion());

        INSTANCE = new AGameOrSomething(1600, 900, false);
        AnEngineOrSomething.DEBUG_RENDER.set(true);

        INSTANCE.init();
        INSTANCE.run();
        INSTANCE.destroy();

        // Terminate GLFW & free
        LOGGER.warn("Terminating program");
    }
}
