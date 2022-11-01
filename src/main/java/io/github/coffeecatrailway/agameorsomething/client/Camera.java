package io.github.coffeecatrailway.agameorsomething.client;

import org.joml.Math;
import org.joml.*;

/**
 * @author CoffeeCatRailway
 * Created: 18/07/2022
 */
public class Camera
{
    public static final float CLIP_NEAR = .3f, CLIP_FAR = 1000f;

    public static final float ZOOM_NEAR = -1f, ZOOM_FAR = -20f;

    private final Vector3f position;
    private final Matrix4f projection;
    private final Matrix4f view;

    private float zoom = ZOOM_FAR;

    public Camera(int width, int height)
    {
        this.position = new Vector3f(0f);
        this.projection = new Matrix4f().perspective((float) (Math.PI / 2f), (float) width / (float) height, CLIP_NEAR, CLIP_FAR);
        this.view = new Matrix4f();
        this.setPosition(new Vector3f(0f, 0f, this.zoom));
    }

    public void zoom(float zoomInc)
    {
        this.zoom = Math.clamp(Camera.ZOOM_FAR, Camera.ZOOM_NEAR, this.zoom + zoomInc);
        this.setPosition(new Vector3f(this.getPosition().x, this.getPosition().y, this.zoom));
    }

    public float getZoom()
    {
        return this.zoom;
    }

    public void setPosition(Vector2fc position)
    {
        this.setPosition(new Vector3f(position.x(), position.y(), this.position.z()));
    }

    public void setPosition(Vector3fc position)
    {
        this.position.set(position);
        this.view.identity().translate(this.position);
    }

    public void addPosition(Vector2fc position)
    {
        this.addPosition(new Vector3f(position.x(), position.y(), 0f));
    }

    public void addPosition(Vector3fc position)
    {
        this.position.add(position);
        this.view.identity().translate(this.position);
    }

    public Vector3f getPosition()
    {
        return this.position;
    }

    public void setProjection(int width, int height)
    {
        this.projection.identity().perspective((float) (Math.PI / 2f), (float) width / (float) height, CLIP_NEAR, CLIP_FAR);
    }

    public Matrix4f getProjection()
    {
        return this.projection;
    }

    public Matrix4f getView()
    {
        return this.view;
    }
}
