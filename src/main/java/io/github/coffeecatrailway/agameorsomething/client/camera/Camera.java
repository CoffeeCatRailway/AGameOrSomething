package io.github.coffeecatrailway.agameorsomething.client.camera;

import io.github.coffeecatrailway.agameorsomething.client.render.MousePicker;
import io.github.coffeecatrailway.agameorsomething.common.utils.TilePos;
import io.github.coffeecatrailway.agameorsomething.common.world.World;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.ocelot.window.Window;
import io.github.ocelot.window.input.KeyboardHandler;
import io.github.ocelot.window.input.MouseHandler;
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
    private final FrustumCullingFilter cullingFilter;

    private float zoom = ZOOM_MAX;

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
        this.cullingFilter = new FrustumCullingFilter();
        this.adjustProjection();
    }

    public void tick()
    {
        KeyboardHandler keyboardHandler = AGameOrSomething.getInstance().getKeyboardHandler();
        if (keyboardHandler.isKeyPressed(GLFW_KEY_UP))
            this.incrementZoom(-ZOOM_SPEED);
        if (keyboardHandler.isKeyPressed(GLFW_KEY_DOWN))
            this.incrementZoom(ZOOM_SPEED);
    }

    public void setPosition(Vector2fc position)
    {
        this.position.set(position);
        this.cullingFilter.updateFrustum(this);
    }

    public Vector2fc getPosition()
    {
        return this.position;
    }

    public void adjustProjection()
    {
        GL11.glViewport(0, 0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.projectionMatrix.identity().perspective(Math.toRadians(70f), (float) this.window.getFramebufferWidth() / (float) this.window.getFramebufferHeight(), Z_NEAR, Z_FAR);
        this.cullingFilter.updateFrustum(this);
    }

    public Matrix4fc getProjectionMatrix()
    {
        return this.projectionMatrix;
    }

    public Matrix4f getViewMatrix()
    {
        return this.viewMatrix.identity().translate(-this.position.x, -this.position.y, -this.zoom);
    }

    public FrustumCullingFilter getCullingFilter()
    {
        return this.cullingFilter;
    }

    public TilePos trace(World world)
    {
        Vector3f startPos = new Vector3f(this.position, this.zoom);
        Vector3f endPos = startPos.add(this.getLook().mul(100), new Vector3f());
        return world.trace(startPos, endPos);
    }

    public Vector3f getLook()
    {
        MouseHandler mouseHandler = AGameOrSomething.getInstance().getMouseHandler();
        return MousePicker.getRay(this.projectionMatrix, this.getViewMatrix(), (float) (mouseHandler.getMouseX() / this.window.getFramebufferWidth() * 2f - 1f), (float) (mouseHandler.getMouseY() / this.window.getFramebufferHeight() * 2f - 1f));
    }

    public void incrementZoom(float zoomInc)
    {
        this.zoom = Math.clamp(ZOOM_MIN, ZOOM_MAX, this.zoom + zoomInc);
        this.cullingFilter.updateFrustum(this);
    }

    public float getZoom()
    {
        return this.zoom;
    }

    public float getZoomInverted()
    {
        return (this.zoom - ZOOM_MIN) * -1f + ZOOM_MAX;
    }
}
