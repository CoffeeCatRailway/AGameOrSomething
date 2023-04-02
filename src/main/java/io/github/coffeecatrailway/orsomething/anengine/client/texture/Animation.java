package io.github.coffeecatrailway.orsomething.anengine.client.texture;

import io.github.coffeecatrailway.orsomething.anengine.common.ObjectLocation;
import org.joml.Math;

/**
 * @author CoffeeCatRailway
 * Created: 25/11/2022
 */
public class Animation
{
    private final ObjectLocation[] baseFrames;
    private ObjectLocation[] frames;
    private int currentIndex = 0;

    private float speed = 1f, timer = 0f;

    public Animation(String id, String subFolder, int frameCount)
    {
        this(new ObjectLocation(id), subFolder, frameCount);
    }

    public Animation(ObjectLocation id, String subFolder, int frameCount)
    {
        this.frames = new ObjectLocation[frameCount];
        for (int i = 0; i < frameCount; i++)
            this.frames[i] = new ObjectLocation(id.getNamespace(), "textures/" + subFolder + "/" + id.getPath() + i);
        this.baseFrames = this.frames;
    }

    public Animation frameOrder(int... frameIndices)
    {
        final ObjectLocation[] old = this.frames;
        this.frames = new ObjectLocation[frameIndices.length];
        for (int i = 0; i < frameIndices.length; i++)
            this.frames[i] = old[frameIndices[i]];
        return this;
    }

    public Animation speed(float speed)
    {
        this.speed = Math.clamp(0f, 10f, speed);
        return this;
    }

    public void tick()
    {
        this.timer += this.speed;
        if (this.timer >= 10f)
        {
            this.currentIndex++;
            this.timer = 0f;
        }
        if (this.currentIndex >= this.frames.length)
            this.currentIndex = 0;
    }

    public ObjectLocation[] getBaseFrames()
    {
        return this.baseFrames;
    }

    public ObjectLocation[] getAllFrames()
    {
        return this.frames;
    }

    public ObjectLocation getCurrentFrame()
    {
        return this.frames[this.currentIndex];
    }
}
