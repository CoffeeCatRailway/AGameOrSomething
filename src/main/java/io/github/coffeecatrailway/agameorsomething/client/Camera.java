package io.github.coffeecatrailway.agameorsomething.client;

import io.github.coffeecatrailway.agameorsomething.common.io.Window;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

/**
 * @author CoffeeCatRailway
 * Created: 18/07/2022
 */
public class Camera
{
    public static final float Z_NEAR = 0f, Z_FAR = 1000f;

    public static final float SCALE_MIN = 8f, SCALE_MAX = 16f;

    private final Vector2f position; // Camera position is double that of tile positions
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;

    private float scale = SCALE_MAX;
    private final Matrix4f scaleMatrix;

    public Camera(Window window)
    {
        this(window, new Vector2f());
    }

    public Camera(Window window, Vector2f position)
    {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.scaleMatrix = new Matrix4f().setTranslation(new Vector3f(0f)).scale(this.scale);
        this.adjustProjection(window);
    }

    public void setPosition(Vector2fc position)
    {
        this.position.set(position);
    }
    public void addPosition(Vector2fc position)
    {
        this.position.add(position);
    }

    public Vector2f getPosition()
    {
        return this.position;
    }

    public void adjustProjection(Window window)
    {
        GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
        this.projectionMatrix.identity().ortho(-window.getWidth() / 2f, window.getWidth() / 2f, -window.getHeight() / 2f, window.getHeight() / 2f, Z_NEAR, Z_FAR);
    }

    public Matrix4f getProjectionMatrix()
    {
        return this.projectionMatrix;
    }

    public Matrix4f getViewMatrix()
    {
        Vector3f front = new Vector3f(0f, 0f, -1f);
        Vector3f up = new Vector3f(0f, 1f, 0f);
        this.viewMatrix.identity().lookAt(new Vector3f(this.position.x, this.position.y, -20f), front.add(this.position.x, this.position.y, 0f), up);
        return this.viewMatrix;
    }

    public void zoom(float zoomInc)
    {
        this.scale = Math.clamp(SCALE_MIN, SCALE_MAX, this.scale + zoomInc);
        this.scaleMatrix.identity().setTranslation(new Vector3f(0f)).scale(this.scale);
    }

    public float getScale()
    {
        return this.scale;
    }

    public float getScaleInverted()
    {
        return (this.scale - SCALE_MIN) * -1f + SCALE_MAX;
    }

    public Matrix4f getScaleMatrix()
    {
        return this.scaleMatrix;
    }
}
