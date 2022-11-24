package io.github.coffeecatrailway.agameorsomething.client.render.shader;

import static org.lwjgl.opengl.GL11C.*;

/**
 * @author CoffeeCatRailway & Ocelot
 * Created: 23/11/2022
 */
public record ShaderAttribute(String name, int components, int dataType)
{
    public int dataSize()
    {
        switch (this.dataType)
        {
            case GL_BYTE, GL_UNSIGNED_BYTE -> {return Byte.BYTES;}
            case GL_SHORT, GL_UNSIGNED_SHORT -> {return Short.BYTES;}
            case GL_INT, GL_UNSIGNED_INT -> {return Integer.BYTES;}
            case GL_FLOAT -> {return Float.BYTES;}
            case GL_DOUBLE -> {return Double.BYTES;}
        }
        return 1;
    }

    @Override
    public String toString()
    {
        return "ShaderAttribute{" +
                "name='" + name + '\'' +
                ", components=" + components +
                ", dataType=" + dataType +
                '}';
    }
}
