package io.github.coffeecatrailway.agameorsomething.client.render;

import org.joml.*;

/**
 * Implementation of a mouse picker to create a ray out of a camera.
 *
 * @author Ocelot
 */
public class MousePicker
{
    private static final Matrix4f INVERSE_PROJECTION = new Matrix4f();
    private static final Matrix4f INVERSE_VIEW = new Matrix4f();

    /**
     * Calculates the ray facing out of the screen.
     *
     * @param projectionMatrix The current projection matrix
     * @param viewMatrix       The current transformed view
     * @param normalizedMouseX The normalized x position. From -1.0 to 1.0 of the viewport
     * @param normalizedMouseY The normalized y position. From -1.0 to 1.0 of the viewport
     * @return A ray pointing out of the camera into the 3D world
     */
    public static Vector3f getRay(Matrix4fc projectionMatrix, Matrix4fc viewMatrix, float normalizedMouseX, float normalizedMouseY)
    {
        Vector4f clipCoords = new Vector4f(normalizedMouseX, -normalizedMouseY, -1.0f, 1.0f);
        Vector4f eyeSpace = toEyeCoords(projectionMatrix, clipCoords);
        return toWorldCoords(viewMatrix, eyeSpace);
    }

    private static Vector4f toEyeCoords(Matrix4fc projectionMatrix, Vector4fc clipCoords)
    {
        Vector4f result = new Vector4f(clipCoords);
        result.mul(projectionMatrix.invert(INVERSE_PROJECTION));
        return result.set(result.x(), result.y(), -1.0f, 0.0f);
    }

    private static Vector3f toWorldCoords(Matrix4fc viewMatrix, Vector4fc eyeCoords)
    {
        Vector4f result = new Vector4f(eyeCoords);
        result.mul(viewMatrix.invert(INVERSE_VIEW));
        return new Vector3f(result.x(), result.y(), result.z()).normalize();
    }
}
