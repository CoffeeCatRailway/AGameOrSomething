package io.github.coffeecatrailway.agameorsomething.client.render.vbo;

import io.github.coffeecatrailway.agameorsomething.client.render.shader.ShaderAttribute;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

/**
 * @author CoffeeCatRailway & Ocelot
 * Created: 23/11/2022
 */
public class VAO
{
    private final int vaoId, vboId;

    private final int vertexCount;
    private final ShaderAttribute[] attributes;
    private int componentsSize;


    public VAO(int vertexCount, List<ShaderAttribute> attributes)
    {
        this(vertexCount, attributes.toArray(ShaderAttribute[]::new));
    }

    public VAO(int vertexCount, ShaderAttribute... attributes)
    {
        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);

        this.vertexCount = vertexCount;
        this.attributes = attributes;
        for (ShaderAttribute attribute : this.attributes)
            this.componentsSize += attribute.components() * attribute.dataSize();

        this.vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
        glBufferData(GL_ARRAY_BUFFER, (long) this.vertexCount * this.componentsSize, GL_DYNAMIC_DRAW);

        int offset = 0;
        for (int i = 0; i < this.attributes.length; i++)
        {
            ShaderAttribute attribute = this.attributes[i];
            glVertexAttribPointer(i, attribute.components(), attribute.dataType(), false, this.componentsSize, offset);
            offset += attribute.components() * attribute.dataSize();
        }

//        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public VAO put(int index, Consumer<ByteBuffer> consumer)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            ByteBuffer buffer = stack.malloc(this.componentsSize);
            consumer.accept(buffer);
            buffer.position(0);
            glBufferSubData(GL_ARRAY_BUFFER, (long) index * this.componentsSize, buffer);
        }
        return this;
    }

    public void draw(int mode, int first, int count)
    {
        glDrawArrays(mode, first, count);
    }

    public void draw(int mode)
    {
        glDrawArrays(mode, 0, this.vertexCount);
    }

    public void bindWrite()
    {
        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
    }

    public void bind()
    {
        glBindVertexArray(this.vaoId);
        for (int i = 0; i < this.attributes.length; i++)
            glEnableVertexAttribArray(i);
    }

    public void unbind()
    {
        for (int i = 0; i < this.attributes.length; i++)
            glDisableVertexAttribArray(i);
        glBindVertexArray(0);
    }
}
