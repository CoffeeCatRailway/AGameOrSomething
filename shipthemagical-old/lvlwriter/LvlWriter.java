package io.github.coffeecatrailway.lvlwriter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.github.coffeecatrailway.shipthemagical.io.Color;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * @package coffeecatteam.lvlwriter
 */
public class LvlWriter
{

    private String level;

    public LvlWriter() {}

    /**
     * LvlWriter.java constructor. Writes tile & entity id's to levels.
     *
     * @param level {@code String}
     */
    public LvlWriter(String level)
    {
        this.level = "./res/levels/" + level;
        try
        {
            BufferedImage tiles = ImageIO.read(new File(this.level + "/tiles.png"));
            BufferedImage entities = ImageIO.read(new File(this.level + "/entities.png"));

            write("tiles", tiles);
            write("entities", entities);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param file  {@code String}
     * @param tiles {@code BufferedImage}
     * @throws IOException
     */
    private void write(String file, BufferedImage tiles) throws IOException
    {
        int width = tiles.getWidth();
        int height = tiles.getHeight();

        int[] colorSheet = tiles.getRGB(0, 0, width, height, null, 0, width);
        String lvl_file = this.level + "/" + file + ".lvl";

        FileWriter lvlFile = new FileWriter(new File(lvl_file), false);

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int index = (colorSheet[x + y * width] >> 16) & 0xFF;
                if (index == 255)
                    index = 0;

                lvlFile.write(index + " ");
            }
            lvlFile.write("\n");
        }
        lvlFile.close();
//        AnsiConsole.out.println("Lvl file: [" + Color.toColor(Color.MAGENTA) + lvl_file + Color.toColor(Color.DEFAULT) + "] writen!");
    }

    /**
     * Deletes .lvl files after game.
     */
    public void delete()
    {
        String tiles = this.level + "/tiles.lvl";
        String entities = this.level + "/entities.lvl";

        new File(tiles).delete();
//        AnsiConsole.out.println("Lvl file: [" + Color.toColor(Color.MAGENTA) + tiles + Color.toColor(Color.DEFAULT) + "] deleted!");

        new File(entities).delete();
//        AnsiConsole.out.println("Lvl file: [" + Color.toColor(Color.MAGENTA) + entities + Color.toColor(Color.DEFAULT) + "] deleted!");
    }
}
