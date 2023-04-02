package io.github.coffeecatrailway.orsomething.anengine.client.camera;

import io.github.coffeecatrailway.orsomething.anengine.common.collision.BoundingBox;
import org.joml.*;

/**
 * @author CoffeeCatRailway
 * Created: 02/01/2023
 */
public class FrustumCullingFilter // TODO: Consider changing to use shaders
{
    private static final Vector3f INSIDE_MIN = new Vector3f();
    private static final Vector3f INSIDE_MAX = new Vector3f();

    private final Matrix4f projectionView;
    private final FrustumIntersection frustum;

    public FrustumCullingFilter()
    {
        this.projectionView = new Matrix4f();
        this.frustum = new FrustumIntersection();
    }

    public void updateFrustum(Camera camera)
    {
        // Calculate projection view matrix
        this.projectionView.set(camera.getProjectionMatrix());
        this.projectionView.mul(camera.getViewMatrix());
//        this.projectionView.mul(new Matrix4f().translate(0f, 0f, -12f)); // Used for debugging
        // Update frustum intersection
        this.frustum.set(this.projectionView, false);
    }

    public boolean isInside(BoundingBox bounds)
    {
        INSIDE_MIN.set(bounds.getPosition(), 0f);
        INSIDE_MAX.set(bounds.getPosition(), 0f).add(bounds.getBounds().x(), bounds.getBounds().y(), 0f);
        return this.frustum.intersectAab(INSIDE_MIN, INSIDE_MAX) < 0;
    }

    public boolean isInside(Vector2fc point)
    {
        return this.frustum.testPoint(point.x(), point.y(), 0f);
    }
}
