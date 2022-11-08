package io.github.coffeecatrailway.agameorsomething.client.render.vbo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author CoffeeCatRailway
 * Created: 07/11/2022
 */
public class VBOModels
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static final VBOModel SIMPLE_1X1;
    public static final VBOModel SIMPLE_1X2;

    static {
        float[] vertices = new float[]
                {
                        -1f, 1f, 0f,    // top left     0
                        1f, 1f, 0f,     // top right    1
                        1f, -1f, 0f,    // bottom right 2
                        -1f, -1f, 0f,   // bottom left  3
                };
        float[] textureCoords = new float[]
                {
                        0f, 0f,
                        1f, 0f,
                        1f, 1f,
                        0f, 1f
                };
        int[] indices = new int[]
                {
                        0, 1, 2,
                        2, 3, 0
                };
        SIMPLE_1X1 = new VBOModel(vertices, textureCoords, indices);

        vertices = new float[]
                {
                        -1f, 3f, 0f,    // top left     0
                        1f, 3f, 0f,     // top right    1
                        1f, -1f, 0f,    // bottom right 2
                        -1f, -1f, 0f,   // bottom left  3
                };
        textureCoords = new float[]
                {
                        0f, 0f,
                        1f, 0f,
                        1f, 1f,
                        0f, 1f
                };
        indices = new int[]
                {
                        0, 1, 2,
                        2, 3, 0
                };
        SIMPLE_1X2 =  new VBOModel(vertices, textureCoords, indices);
    }

    public static void deleteStaticModels()
    {
        SIMPLE_1X1.delete();
        SIMPLE_1X2.delete();
        LOGGER.warn("Public static models deleted!");
    }
}
