package io.github.coffeecatrailway.agameorsomething.client.render;

import io.github.coffeecatrailway.agameorsomething.common.io.ResourceLoader;
import io.github.coffeecatrailway.agameorsomething.registry.ObjectLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author CoffeeCatRailway
 * Created: 15/07/2022
 */
public class Shader
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static final int ATTRIB_POSITION = 0, ATTRIB_TEX_COORDS = 1;

    private final int program;
    private final int vertex;
    private final int fragment;

    private final ObjectLocation vertexLocation;
    private final ObjectLocation fragmentLocation;

    public Shader(String shader)
    {
        this(new ObjectLocation("shaders/" + shader + ".vert"), new ObjectLocation("shaders/" + shader + ".frag"));
    }

    public Shader(ObjectLocation vertexLocation, ObjectLocation fragmentLocation)
    {
        this.program = glCreateProgram();
        this.vertexLocation = vertexLocation;
        this.fragmentLocation = fragmentLocation;

        this.vertex = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(this.vertex, this.readShaderFile(vertexLocation));
        glCompileShader(this.vertex);
        if (glGetShaderi(this.vertex, GL_COMPILE_STATUS) != GL_TRUE)
        {
            LOGGER.error(glGetShaderInfoLog(this.vertex));
            throw new RuntimeException("Vertex shader " + vertexLocation + " could not compile!");
        }

        this.fragment = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(this.fragment, this.readShaderFile(fragmentLocation));
        glCompileShader(this.fragment);
        if (glGetShaderi(this.fragment, GL_COMPILE_STATUS) != GL_TRUE)
        {
            LOGGER.error(glGetShaderInfoLog(this.fragment));
            throw new RuntimeException("Fragment shader " + fragmentLocation + " could not compile!");
        }

        glAttachShader(this.program, this.vertex);
        glAttachShader(this.program, this.fragment);

        glBindAttribLocation(this.program, ATTRIB_POSITION, "position");
        glBindAttribLocation(this.program, ATTRIB_TEX_COORDS, "texCoords");

        glLinkProgram(this.program);
        if (glGetProgrami(this.program, GL_LINK_STATUS) != GL_TRUE)
        {
            LOGGER.error(glGetProgramInfoLog(this.program));
            LOGGER.error("Vertex shader: {}", vertexLocation);
            LOGGER.error("Fragment shader: {}", fragmentLocation);
            throw new RuntimeException("Something's wrong with shader " + this.program);
        }
        glValidateProgram(this.program);
        if (glGetProgrami(this.program, GL_VALIDATE_STATUS) != GL_TRUE)
        {
            LOGGER.error(glGetProgramInfoLog(this.program));
            LOGGER.error("Vertex shader: {}", vertexLocation);
            LOGGER.error("Fragment shader: {}", fragmentLocation);
            throw new RuntimeException("Something's wrong with shader (" + this.program);
        }
    }

    public void setUniform(String name, int value)
    {
        int location = glGetUniformLocation(this.program, name);
        if (location != -1)
            glUniform1i(location, value);
    }

    public void setUniform(String name, float value)
    {
        int location = glGetUniformLocation(this.program, name);
        if (location != -1)
            glUniform1f(location, value);
    }

    public void setUniform(String name, Matrix4f value)
    {
        int location = glGetUniformLocation(this.program, name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);
        if (location != -1)
            glUniformMatrix4fv(location, false, buffer);
    }

    public void bind()
    {
        glUseProgram(this.program);
    }

    public void unbind()
    {
        glUseProgram(0);
    }

    public void delete()
    {
        glDetachShader(this.program, this.vertex);
        glDetachShader(this.program, this.fragment);
        glUseProgram(0);
        LOGGER.debug("Detached shader {} Vertex: {} Fragment: {}", this.program, this.vertexLocation, this.fragmentLocation);
        glDeleteShader(this.vertex);
        glDeleteShader(this.fragment);
        glDeleteProgram(this.program);
        LOGGER.debug("Deleted shader {} Vertex: {} Fragment: {}", this.program, this.vertexLocation, this.fragmentLocation);
    }

    private String readShaderFile(ObjectLocation location)
    {
        StringBuilder string = new StringBuilder();
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(ResourceLoader.getResource(location).openStream()));
            String line;
            while ((line = br.readLine()) != null)
            {
                string.append(line);
                string.append("\n");
            }
            br.close();
        } catch (IOException e)
        {
            LOGGER.error("Something went wrong reading shader {}!", location, e);
            e.printStackTrace();
        }
        return string.toString();
    }
}
