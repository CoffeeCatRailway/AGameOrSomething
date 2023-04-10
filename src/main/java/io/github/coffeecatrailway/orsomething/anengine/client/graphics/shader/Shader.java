package io.github.coffeecatrailway.orsomething.anengine.client.graphics.shader;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.orsomething.anengine.core.io.ObjectLocation;
import io.github.coffeecatrailway.orsomething.anengine.core.io.ResourceLoader;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.*;

/**
 * @author CoffeeCatRailway
 * Created: 15/07/2022
 */
public class Shader
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final List<ShaderAttribute> DEFAULT_ATTRIBUTES = List.of(new ShaderAttribute("position", 3, GL_FLOAT),
            new ShaderAttribute("texCoords", 2, GL_FLOAT),
            new ShaderAttribute("texColor", 4, GL_FLOAT));

    private final int program;
    private final int vertex;
    private final int fragment;

    private final ObjectLocation vertexLocation;
    private final ObjectLocation fragmentLocation;

    public Shader(String shader)
    {
        this(new ObjectLocation("shaders/" + shader + ".vert"), new ObjectLocation("shaders/" + shader + ".frag"));
    }

    public Shader(String vert, String frag)
    {
        this(new ObjectLocation("shaders/" + vert + ".vert"), new ObjectLocation("shaders/" + frag + ".frag"));
    }

    public Shader(ObjectLocation vertexLocation, ObjectLocation fragmentLocation)
    {
        this.program = glCreateProgram();
        this.vertexLocation = vertexLocation;
        this.fragmentLocation = fragmentLocation;

        this.vertex = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(this.vertex, ResourceLoader.readToString(vertexLocation));
        glCompileShader(this.vertex);
        if (glGetShaderi(this.vertex, GL_COMPILE_STATUS) != GL_TRUE)
        {
            LOGGER.error(glGetShaderInfoLog(this.vertex));
            throw new RuntimeException("Vertex shader " + vertexLocation + " could not compile!");
        }

        this.fragment = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(this.fragment, ResourceLoader.readToString(fragmentLocation));
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

    public void setUniformBlockf(String name, final int binding, float[] data)
    {
        int index = glGetUniformBlockIndex(this.program, name);
        if (index != GL_INVALID_INDEX)
        {
            glUniformBlockBinding(this.program, index, binding);

            int buffer = glGenBuffers();
            glBindBuffer(GL_UNIFORM_BUFFER, buffer);

            glBufferData(GL_UNIFORM_BUFFER, (long) data.length * GL_FLOAT, GL_DYNAMIC_DRAW);
            glBufferSubData(GL_UNIFORM_BUFFER, 0, data);
            glBindBufferBase(GL_UNIFORM_BUFFER, binding, buffer);
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

    public void setUniformVector2f(String name, Vector2fc vec)
    {
        setUniformVector2f(name, vec.x(), vec.y());
    }

    public void setUniformVector2f(String name, float x, float y)
    {
        int location = glGetUniformLocation(this.program, name);
        if (location != -1)
            glUniform2f(location, x, y);
    }

    public void setUniformVector3f(String name, Vector3fc vec)
    {
        setUniformVector3f(name, vec.x(), vec.y(), vec.z());
    }

    public void setUniformVector3f(String name, float x, float y, float z)
    {
        int location = glGetUniformLocation(this.program, name);
        if (location != -1)
            glUniform3f(location, x, y, z);
    }

    public void setUniformVector4f(String name, Vector4fc vec)
    {
        setUniformVector4f(name, vec.x(), vec.y(), vec.z(), vec.w());
    }

    public void setUniformVector4f(String name, float x, float y, float z, float w)
    {
        int location = glGetUniformLocation(this.program, name);
        if (location != -1)
            glUniform4f(location, x, y, z, w);
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
}
