package io.github.coffeecatrailway.orsomething.anengine.client.camera;

import io.github.coffeecatrailway.orsomething.anengine.client.MousePicker;
import io.github.coffeecatrailway.orsomething.anengine.common.world.TilePos;
import io.github.coffeecatrailway.orsomething.anengine.common.world.World;
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

    // Zoom
    public static final float ZOOM_MIN = 8f, ZOOM_MAX = 32f, ZOOM_SPEED = 1f / 4f;
    private float zoom = (ZOOM_MAX - ZOOM_MIN) / 2f + ZOOM_MIN;

    private final Window window;
    private final Vector2f position; // Camera position is double that of tile positions
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private final FrustumCullingFilter cullingFilter;

    // Following
    private static final Vector2f FOLLOW_POSITION = new Vector2f();
    private static final float FOLLOW_SMOOTHNESS = .15f;
    private final Vector2f followPosition = new Vector2f();
    private boolean keepFollowing = true;

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

    public void tick(KeyboardHandler keyboardHandler)
    {
        if (keyboardHandler.isKeyPressed(GLFW_KEY_UP))
            this.incrementZoom(-ZOOM_SPEED);
        if (keyboardHandler.isKeyPressed(GLFW_KEY_DOWN))
            this.incrementZoom(ZOOM_SPEED);

        if (this.keepFollowing)
            this.setPosition(this.position.lerp(this.followPosition.add(.5f, .5f, FOLLOW_POSITION), FOLLOW_SMOOTHNESS, FOLLOW_POSITION));
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

    public TilePos trace(World world, MouseHandler mouseHandler)
    {
        Vector3f startPos = new Vector3f(this.position, this.zoom);
        Vector3f endPos = startPos.add(this.getLook(mouseHandler).mul(100), new Vector3f());
        return world.trace(startPos, endPos);
    }

    public Vector3f getLook(MouseHandler mouseHandler)
    {
        return MousePicker.getRay(this.projectionMatrix, this.getViewMatrix(), (float) (mouseHandler.getMouseX() / this.window.getFramebufferWidth() * 2f - 1f), (float) (mouseHandler.getMouseY() / this.window.getFramebufferHeight() * 2f - 1f));
    }

    public void follow(Vector2fc position)
    {
        this.followPosition.set(position);
    }

    public void setKeepFollowing(boolean keepFollowing)
    {
        this.keepFollowing = keepFollowing;
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
