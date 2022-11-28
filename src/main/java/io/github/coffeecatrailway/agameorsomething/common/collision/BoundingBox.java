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
                && this.position.x + this.bounds.x >= other.position.x
                && this.position.y < other.position.y + other.bounds.y
                && this.position.y + this.bounds.y >= other.position.y;
    }

    private Vector2f getCenter()
    {
        Vector2f ret = new Vector2f();
        return this.position.add(this.bounds.div(2f, ret), ret);
    }

    private Vector2f getOverlap(BoundingBox other)
    {
        return this.getCenter().sub(other.getCenter());
    }
    
    public void correctPosition(BoundingBox other, Vector2f toCorrect)
    {
        Vector2f overlap = this.getOverlap(other);
        if (overlap.x * overlap.x > overlap.y * overlap.y)
        {
            if (overlap.x < 0f)
                this.position.x = other.position.x - this.bounds.x;
            else if (overlap.x > 0f)
                this.position.x = other.position.x + other.bounds.x;
        } else
        {
            if (overlap.y < 0f)
                this.position.y = other.position.y - this.bounds.y;
            else if (overlap.y > 0f)
                this.position.y = other.position.y + other.bounds.y;
        }
        toCorrect.set(this.position);
    }

    public void setPosition(Vector2fc position)
    {
        this.position.set(position);
    }
}
