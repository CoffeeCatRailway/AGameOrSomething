package linerenderer;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.orsomething.anengine.client.camera.Camera;
import io.github.ocelot.window.Window;
import io.github.ocelot.window.WindowEventListener;
import io.github.ocelot.window.WindowManager;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.slf4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main implements WindowEventListener
{
    public static final Logger LOGGER = LogUtils.getLogger();

    private final WindowManager windowManager;
    private final Window window;

    private Camera camera;

    public Main()
    {
        this.windowManager = new WindowManager();
        this.window = this.windowManager.create(800, 600, false);
        this.window.setFullscreen(false);
        this.window.addListener(this);
    }

    private void init()
    {
        // Configure
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

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
        this.camera.setKeepFollowing(false);
    }

    private void loop()
    {
        float[] lineVertices = new float[]{
                -5f, -5f,
                -5f, 5f,
                5f, 0f,
                -5f, -5f
        };

        glClearColor(0f, 0f, 0f, 0f);

        while (!this.window.isClosed())
        {
            this.windowManager.update();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            LineRenderer.INSTANCE.updateUniforms(this.camera);

            LineRenderer.INSTANCE.begin(1f, 0f, 0f);
            LineRenderer.INSTANCE.draw(lineVertices);
            LineRenderer.INSTANCE.end();
        }
    }

    private void destroy()
    {
        LineRenderer.SHADER.delete();
        this.windowManager.free();
    }

    @Override
    public void framebufferResized(Window window, int width, int height)
    {
        this.camera.adjustProjection();
    }

    public static void main(String[] args)
    {
        LOGGER.info("LWJGL version: " + Version.getVersion());

        Main instance = new Main();

        instance.init();
        instance.loop();
        instance.destroy();

        // Terminate GLFW & free
        LOGGER.warn("Terminating program");
    }
}
