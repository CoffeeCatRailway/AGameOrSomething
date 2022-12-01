package io.github.coffeecatrailway.agameorsomething.common.collision;

import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * @author CoffeeCatRailway
 * Created: 27/11/2022
 */
public class BoundingBox
{
    public static final float COLLISION_PADDING = .001f;

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

    public boolean isIntersecting(BoundingBox other)
    {
        return this.position.x < other.position.x + other.bounds.x
                && this.position.x + this.bounds.x > other.position.x
                && this.position.y < other.position.y + other.bounds.y
                && this.position.y + this.bounds.y > other.position.y;
    }

    private Vector2f getCenter()
    {
        Vector2f ret = new Vector2f();
        this.bounds.div(2f, ret);
        return this.position.add(ret, ret);
    }
    
    public void correctAndStop(BoundingBox other)
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

    public void setPosition(Vector2fc position)
    {
        this.position.set(position);
    }

    public Vector2fc getBounds()
    {
        return this.bounds;
    }
}
