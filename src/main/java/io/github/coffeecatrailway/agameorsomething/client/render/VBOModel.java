package io.github.coffeecatrailway.agameorsomething.client.render;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL33.*;

/**
 * @author CoffeeCatRailway
 * Created: 15/07/2022
 */
public class VBOModel
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final int drawCount;

    private final int vaoId;

    private final int vertexId;
    private final int indicesId;

    public VBOModel(float[] vertices, float[] textureCoords, int[] indices)
    {
        this.drawCount = indices.length;

        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);

        this.vertexId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vertexId);
        glBufferData(GL_ARRAY_BUFFER, (long) (vertices.length + textureCoords.length) * Float.BYTES, GL_STATIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, createFloatBuffer(vertices));
        glBufferSubData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, createFloatBuffer(textureCoords));

        glVertexAttribPointer(Shader.ATTRIB_POSITION, 3, GL_FLOAT, false, 0, 0L);
        glVertexAttribPointer(Shader.ATTRIB_TEX_COORDS, 2, GL_FLOAT, false, 0, (long) vertices.length * Float.BYTES);

        this.indicesId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.indicesId);
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices);
        indicesBuffer.flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render()
    {
        glBindVertexArray(this.vaoId);

        glEnableVertexAttribArray(Shader.ATTRIB_POSITION);
        glEnableVertexAttribArray(Shader.ATTRIB_TEX_COORDS);

        glDrawElements(GL_TRIANGLES, this.drawCount, GL_UNSIGNED_INT, 0L);

        glDisableVertexAttribArray(Shader.ATTRIB_POSITION);
        glDisableVertexAttribArray(Shader.ATTRIB_TEX_COORDS);

        glBindVertexArray(0);
    }

    public void delete()
    {
        LOGGER.debug("Deleting vao...");
        glDeleteVertexArrays(this.vaoId);
        LOGGER.debug("Deleting vbo model...");
        glDeleteBuffers(this.vertexId);
        glDeleteBuffers(this.indicesId);
    }

    private FloatBuffer createFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}
