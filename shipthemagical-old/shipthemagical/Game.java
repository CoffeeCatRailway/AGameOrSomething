package io.github.coffeecatrailway.shipthemagical;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL;

import io.github.coffeecatrailway.lvlwriter.LvlWriter;
import io.github.coffeecatrailway.shipthemagical.assets.Assets;
import io.github.coffeecatrailway.shipthemagical.assets.PlayerAssets;
import io.github.coffeecatrailway.shipthemagical.gui.guis.GuiPause;
import io.github.coffeecatrailway.shipthemagical.io.Config;
import io.github.coffeecatrailway.shipthemagical.io.Config.PropertyType;
import io.github.coffeecatrailway.shipthemagical.io.Timer;
import io.github.coffeecatrailway.shipthemagical.io.Window;
import io.github.coffeecatrailway.shipthemagical.render.Camera;
import io.github.coffeecatrailway.shipthemagical.render.Shader;
import io.github.coffeecatrailway.shipthemagical.world.TileRenderer;
import io.github.coffeecatrailway.shipthemagical.world.World;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 */
public class Game
{

    private static int width = 640;
    private static int height = 480;

    private Window window;
    public Config config;

    /**
     * Game.java constructor.
     */
    public Game()
    {
        Window.setCallbacks();
        config = new Config("game.config", Config.RES);

        width = (int) config.getProperty("game.width", PropertyType.INT);
        height = (int) config.getProperty("game.height", PropertyType.INT);

        if (!glfwInit())
        {
            throw new IllegalStateException("Failed to initialize GLFW!");
        }

        window = new Window();
        window.setSize(width, height);
        window.createWindow((String) config.getProperty("game.title", PropertyType.STRING));

        try
        {
            BufferedImage[] icons = {
                    ImageIO.read(new File("./res/icon64x64.png")),
                    ImageIO.read(new File("./res/icon32x32.png"))
            };
            // ImageIO.read(new File("./res/icon.png"));
            window.setIcon(window.getWindow(), icons);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        glfwSwapInterval(0);
        GL.createCapabilities();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        if ((boolean) config.getProperty("game.hideCursor", PropertyType.BOOLEAN))
        {
            glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        }

        Camera camera = new Camera(window.getWidth(), window.getHeight());
        glEnable(GL_TEXTURE_2D);

        TileRenderer tiles = new TileRenderer();

        PlayerAssets playerAssets = new PlayerAssets();
        Assets.initAsset();
        playerAssets.initAsset();

        Shader shader = new Shader("shader");

        String level = (String) config.getProperty("game.level", PropertyType.STRING);
        LvlWriter lvlwriter = new LvlWriter();
        if ((boolean) config.getProperty("game.rewriteLevel", PropertyType.BOOLEAN))
            lvlwriter = new LvlWriter(level);

        World world = new World(level, camera);
        world.calcView(window);

        GuiPause guiPause = new GuiPause(window);
        // GuiMainMenu guiMainMenu = new GuiMainMenu(window);

        double perSeceond = (double) config.getProperty("game.fps.perSeceond", PropertyType.DOUBLE);
        double maxFps = (double) config.getProperty("game.fps.maxFps", PropertyType.DOUBLE);

        double frame_cap = perSeceond / maxFps;
        double frame_time = 0;
        int frames = 0;

        double time = Timer.getTime();
        double unprocessed = 0;

        /**
         * Main game loop
         */
        while (!window.shouldClose())
        {
            boolean canRender = false;

            double time_2 = Timer.getTime();
            double passed = time_2 - time;
            unprocessed += passed;
            frame_time += passed;

            time = time_2;

            /**
             * Main logic loop
             */
            while (unprocessed >= frame_cap)
            {
                if (window.hasResized())
                {
                    camera.setProjection(window.getWidth(), window.getHeight());
                    guiPause.resizeCamera(window);
                    world.calcView(window);
                    glViewport(0, 0, window.getWidth(), window.getHeight());
                }

                unprocessed -= frame_cap;
                canRender = true;

                guiPause.update(window.getInput());

                if (!guiPause.render)
                {
                    world.update((float) frame_cap, window, shader, camera);
                    world.correctCamera((float) frame_cap, camera, window);
                }

                window.update();
                if (frame_time >= 1.0)
                {
                    frame_time = 0;
                    System.out.println("[Game] FPS: " + frames);
                    frames = 0;
                }
            }

            if (canRender)
            {
                glClear(GL_COLOR_BUFFER_BIT);

                world.render(tiles, shader, camera);
                guiPause.render();

                window.swapBuffers();
                frames++;
            }
        }

        Assets.deleteAsset();
        playerAssets.deleteAsset();
        if ((boolean) config.getProperty("game.deleteLevel", PropertyType.BOOLEAN))
            lvlwriter.delete();
        glfwTerminate();
    }

    /**
     * @param args {@code String[]}
     */
    public static void main(String[] args)
    {
        new Game();
    }
}
