package io.github.coffeecatrailway.agameorsomething.client.render.shader;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.agameorsomething.common.io.ResourceLoader;
import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;
import org.joml.Matrix4fc;
import org.joml.Vector4fc;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author CoffeeCatRailway
 * Created: 15/07/2022
 */
public class Shader
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int ATTRIB_POSITION = 0, ATTRIB_TEX_COORDS = 1, ATTRIB_COLOR = 2;
    public static final List<ShaderAttribute> DEFAULT_ATTRIBUTES = List.of(new ShaderAttribute("position", 3, GL_FLOAT),
            new ShaderAttribute("texCoords", 2, GL_FLOAT),
            new ShaderAttribute("texColor", 4, GL_FLOAT));

    public static final Shader SIMPLE = new Shader("simple", DEFAULT_ATTRIBUTES);
    public static final Shader SPRITE = new Shader("sprite", DEFAULT_ATTRIBUTES);

    private final int program;
    private final int vertex;
    private final int fragment;

    private final ObjectLocation vertexLocation;
    private final ObjectLocation fragmentLocation;

    public Shader(String shader, List<ShaderAttribute> attributes)
    {
        this(shader, attributes.toArray(ShaderAttribute[]::new));
    }

    public Shader(String shader, ShaderAttribute... attributes)
    {
        this(new ObjectLocation("shaders/" + shader + ".vert"), new ObjectLocation("shaders/" + shader + ".frag"), attributes);
    }

    public Shader(ObjectLocation vertexLocation, ObjectLocation fragmentLocation, ShaderAttribute... attributes)
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

    public void setUniformi(String name, int value)
    {
        int location = glGetUniformLocation(this.program, name);
        if (location != -1)
            glUniform1i(location, value);
    }

    public void setUniformf(String name, float value)
    {
        int location = glGetUniformLocation(this.program, name);
        if (location != -1)
            glUniform1f(location, value);
    }

    public void setUniformMatrix4f(String name, Matrix4fc value)
    {
        int location = glGetUniformLocation(this.program, name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);
        if (location != -1)
            glUniformMatrix4fv(location, false, buffer);
    }

    public void setUniformVector4f(String name, Vector4fc value)
    {
        int location = glGetUniformLocation(this.program, name);
        if (location != -1)
            glUniform4f(location, value.x(), value.y(), value.z(), value.w());
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

    public static void deleteStaticShaders()
    {
        SIMPLE.delete();
        SPRITE.delete();
        LOGGER.warn("Static shaders deleted!");
    }
}
