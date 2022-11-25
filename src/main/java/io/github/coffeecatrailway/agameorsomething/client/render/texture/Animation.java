package io.github.coffeecatrailway.agameorsomething.client.render.texture;

import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;
import org.joml.Math;

/**
 * @author CoffeeCatRailway
 * Created: 25/11/2022
 */
public class Animation
{
    private final ObjectLocation[] frames;
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
    }

    public Animation frameOrder(int... frameIndices)
    {
        final ObjectLocation[] old = this.frames;
        for (int i = 0; i < old.length; i++)
            this.frames[frameIndices[i]] = old[i];
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

    public ObjectLocation getCurrentFrame()
    {
        return this.frames[this.currentIndex];
    }

    public ObjectLocation[] getAllFrames()
    {
        return this.frames;
    }
}
