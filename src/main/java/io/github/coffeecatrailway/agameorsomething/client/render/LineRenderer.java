package io.github.coffeecatrailway.agameorsomething.client.render;

import io.github.coffeecatrailway.agameorsomething.common.collision.BoundingBox;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author CoffeeCatRailway
 * Created: 16/12/2022
 *
 * Used for debuging
 */
public final class LineRenderer
{
    private static final float[] MATRIX_FLOAT_ARRAY = new float[16];
    private static float WIDTH = 2f;
    private static final Vector3f COLOR = new Vector3f(1f, 0f, 0f);

    private LineRenderer()
    {}

    public static void drawBoundingBox(BoundingBox box)
    {
        drawBox(box.getPosition(), box.getPosition().add(box.getBounds(), new Vector2f()));
    }

    public static void drawBox(Vector2fc bottomLeft, Vector2fc topRight)
    {
        drawLineStrip(bottomLeft.x(), bottomLeft.y(),
                topRight.x(), bottomLeft.y(),
                topRight.x(), topRight.y(),
                bottomLeft.x(), topRight.y(),
                bottomLeft.x(), bottomLeft.y());
    }

    public static void drawLine(Vector2fc start, Vector2fc end)
    {
        drawLineStrip(start.x(), start.y(), end.x(), end.y());
    }

    public static void drawLineStrip(float... vertices)
    {
        assert vertices.length % 2 == 0;
        assert vertices.length >= 4;

        glEnable(GL_LINE_SMOOTH);
        glDisable(GL_TEXTURE_2D);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMultMatrixf(AGameOrSomething.getInstance().getCamera().getProjectionMatrix().get(MATRIX_FLOAT_ARRAY));
        glMultMatrixf(AGameOrSomething.getInstance().getCamera().getViewMatrix().get(MATRIX_FLOAT_ARRAY));

        glColor3f(COLOR.x, COLOR.y, COLOR.z);
        glLineWidth(WIDTH);

        glBegin(GL_LINE_STRIP);
        for (int i = 0; i < vertices.length; i += 2)
            glVertex2f(vertices[i], vertices[i + 1]);
        glEnd();

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
    }

    public static void setLineWidth(float width)
    {
        WIDTH = width;
    }

    public static void setLineColor(float r, float g, float b)
    {
        COLOR.set(r, g, b);
    }
}
