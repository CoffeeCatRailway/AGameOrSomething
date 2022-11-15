package io.github.coffeecatrailway.agameorsomething.client.render.texture;

import io.github.coffeecatrailway.agameorsomething.common.io.ResourceLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author CoffeeCatRailway
 * Created: 16/11/2022
 */
public class TextureAtlas
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static final Path TEMP_ATLAS_PATH = Paths.get("./temp/atlas");
    public static final File TEMP_ATLAS_DIR = TEMP_ATLAS_PATH.toFile();

    public static void init()
    {
        try
        {
            if (!TEMP_ATLAS_DIR.exists())
                Files.createDirectories(TEMP_ATLAS_PATH);

            BufferedImage atlas = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);

            BufferedImage missing = ImageIO.read(ResourceLoader.getResource("textures/missing.png"));
            BufferedImage dirt = ImageIO.read(ResourceLoader.getResource("textures/tile/dirt.png"));

            Graphics graphics = atlas.getGraphics();
            graphics.drawImage(missing, 0, 0, null);
            graphics.drawImage(dirt, missing.getWidth(), 0, null);
            graphics.dispose();

            ImageIO.write(atlas, "PNG", new File(TEMP_ATLAS_DIR, "atlas.png"));
        } catch (IOException e)
        {
            LOGGER.error(e);
            e.printStackTrace();
        }
    }
}
