package io.github.coffeecatrailway.agameorsomething.client;

import io.github.coffeecatrailway.agameorsomething.client.render.MousePicker;
import io.github.coffeecatrailway.agameorsomething.common.io.InputHandler;
import io.github.coffeecatrailway.agameorsomething.common.io.Window;
import io.github.coffeecatrailway.agameorsomething.common.utils.TilePos;
import io.github.coffeecatrailway.agameorsomething.common.world.World;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

/**
 * @author CoffeeCatRailway
 * Created: 18/07/2022
 */
public class Camera
{
    public static final float Z_NEAR = 0f, Z_FAR = 1000f;

    public static final float ZOOM_MIN = 8f, ZOOM_MAX = 16f, ZOOM_SPEED = 1f / 4f;

    private final Window window;
    private final Vector2f position; // Camera position is double that of tile positions
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;

    private float zoom = ZOOM_MAX;
//    private final Matrix4f scaleMatrix;

    public Camera(Window window)
    {
        this(window, new Vector2f());
    }

    public Camera(Window window, Vector2f position)
    {
        this.window = window;
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
//        this.scaleMatrix = new Matrix4f().setTranslation(new Vector3f(0f)).scale(this.scale);
        this.adjustProjection();
    }

    public void tick()
    {
        if (Window.getInputHandler().isKeyDown(GLFW_KEY_UP))
            this.incrementZoom(-ZOOM_SPEED);
        if (Window.getInputHandler().isKeyDown(GLFW_KEY_DOWN))
            this.incrementZoom(ZOOM_SPEED);
    }

    public void setPosition(Vector2fc position)
    {
        this.position.set(position);
    }

    public void addPosition(Vector2fc position)
    {
        this.position.add(position);
    }

    public Vector2fc getPosition()
    {
        return this.position;
    }

    public void adjustProjection()
    {
        GL11.glViewport(0, 0, this.window.getWidth(), this.window.getHeight());
//        this.projectionMatrix.identity().ortho(-this.window.getWidth() / 2f, this.window.getWidth() / 2f, -this.window.getHeight() / 2f, this.window.getHeight() / 2f, Z_NEAR, Z_FAR);
        this.projectionMatrix.identity().perspective(Math.toRadians(70), (float) this.window.getWidth() / (float) this.window.getHeight(), Z_NEAR, Z_FAR);
    }

    public Matrix4fc getProjectionMatrix()
    {
        return this.projectionMatrix;
    }

    public Matrix4f getViewMatrix()
    {
//        Vector3f front = new Vector3f(0f, 0f, -1f);
//        Vector3f up = new Vector3f(0f, 1f, 0f);
//        this.viewMatrix.identity().lookAt(new Vector3f(this.position.x, this.position.y, -20f), front.add(this.position.x, this.position.y, 0f), up);
        return this.viewMatrix.identity().translate(-this.position.x, -this.position.y, -this.zoom);
    }

    public TilePos trace(World world)
    {
        Vector3f startPos = new Vector3f(this.position, this.zoom);
        Vector3f endPos = startPos.add(this.getLook().mul(100), new Vector3f());
        return world.trace(startPos, endPos);
    }

    public Vector3f getLook()
    {
        return MousePicker.getRay(this.projectionMatrix, this.getViewMatrix(), InputHandler.getMousePosition().x() / this.window.getWidth() * 2f - 1f, InputHandler.getMousePosition().y() / this.window.getHeight() * 2f - 1f);
    }

    public void incrementZoom(float zoomInc)
    {
        this.zoom = Math.clamp(ZOOM_MIN, ZOOM_MAX, this.zoom + zoomInc);
//        this.scaleMatrix.identity().setTranslation(new Vector3f(0f)).scale(this.scale);
    }

    public float getZoom()
    {
        return this.zoom;
    }

    public float getZoomInverted()
    {
        return (this.zoom - ZOOM_MIN) * -1f + ZOOM_MAX;
    }

//    public Matrix4f getScaleMatrix()
//    {
//        return this.scaleMatrix;
//    }
}
