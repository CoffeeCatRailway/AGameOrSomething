package io.github.coffeecatrailway.agameorsomething.common.collision;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

/**
 * @author CoffeeCatRailway
 * Created: 27/11/2022
 */
public class BoundingBox
{
    public static final float COLLISION_PADDING = .001f;
    public static final BoundingBox EMPTY = new BoundingBox(new Vector2f(0f), new Vector2f(0f));

    private final Vector2f center = new Vector2f();

    private final Vector2f position;
    private final Vector2f bounds = new Vector2f();

    public BoundingBox(Vector2f position, float width, float height)
    {
        this(position, new Vector2f(width, height));
    }

    public BoundingBox(Vector2f position, Vector2fc bounds)
    {
        this.position = position;
        bounds.sub(new Vector2f(COLLISION_PADDING), this.bounds); // "fixed"
    }

    public BoundingBox(Vector2f position, Vector2ic bounds)
    {
        this.position = position;
        this.bounds.set((float) bounds.x() - COLLISION_PADDING, (float) bounds.y() - COLLISION_PADDING); // "fixed"
    }

    public boolean isIntersecting(BoundingBox other)
    {
        return this.position.x < other.position.x + other.bounds.x
                && this.position.x + this.bounds.x > other.position.x
                && this.position.y < other.position.y + other.bounds.y
                && this.position.y + this.bounds.y > other.position.y;
    }

    private Vector2f getCenter()
    {
        this.bounds.div(2f, this.center);
        return this.center.add(this.position);
    }

    public void correctAndStop(BoundingBox other) // TODO: Fix sticking when hugging a wall
    {
        Vector2f correction = this.getCenter().sub(other.getCenter());
        if (correction.x * correction.x > correction.y * correction.y)
        {
            if (correction.x < 0f)
                this.position.x = other.position.x - this.bounds.x;
            else if (correction.x > 0f)
                this.position.x = other.position.x + other.bounds.x;
        } else
        {
            if (correction.y < 0f)
                this.position.y = other.position.y - this.bounds.y;
            else if (correction.y > 0f)
                this.position.y = other.position.y + other.bounds.y;
        }
    }

    public void correctAndPush(BoundingBox other)
    {
        this.position.add(this.getCenter().sub(other.getCenter()).normalize().div(24f));
    }

    public Vector2fc getPosition()
    {
        return this.position;
    }

    public void setPosition(Vector2fc position)
    {
        this.position.set(position);
    }

    public Vector2fc getBounds()
    {
        return this.bounds;
    }
}
