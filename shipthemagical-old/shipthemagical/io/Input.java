package io.github.coffeecatrailway.shipthemagical.io;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class Input {

	private long window;
	private boolean keys[];
	private boolean mouseButtons[];

	private static Vector2f mousePos = new Vector2f();
	private static double[] x = new double[1], y = new double[1];
	private static int[] winWidth = new int[1], winHeight = new int[1];

	/**
	 * Input.java constructor.
	 * 
	 * @param window {@code Long}
	 */
	public Input(long window) {
		this.window = window;
		this.keys = new boolean[GLFW_KEY_LAST];
		this.mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];

		for (int i = 32; i < GLFW_KEY_LAST; i++)
			keys[i] = false;
		for (int i = 32; i < GLFW_MOUSE_BUTTON_LAST; i++)
			mouseButtons[i] = false;
	}

	/**
	 * Check if key is down.
	 * 
	 * @param key {@code Integer}
	 * @return {@code Boolean}
	 */
	public boolean isKeyDown(int key) {
		return glfwGetKey(window, key) == 1;
	}

	/**
	 * Check if key is pressed.
	 * 
	 * @param key {@code Integer}
	 * @return {@code Boolean}
	 */
	public boolean isKeyPressed(int key) {
		return (isKeyDown(key) && !keys[key]);
	}

	/**
	 * Check if key is released.
	 * 
	 * @param key {@code Integer}
	 * @return {@code Boolean}
	 */
	public boolean isKeyReleased(int key) {
		return (!isKeyDown(key) && keys[key]);
	}

	/**
	 * Check if mouse button is down.
	 * 
	 * @param button {@code Integer}
	 * @return {@code Boolean}
	 */
	public boolean isMouseButtonDown(int button) {
		return glfwGetMouseButton(window, button) == 1;
	}

	/**
	 * Check if mouse button is pressed.
	 * 
	 * @param button {@code Integer}
	 * @return {@code Boolean}
	 */
	public boolean isMouseButtonPressed(int button) {
		return (isMouseButtonDown(button) && !mouseButtons[button]);
	}

	/**
	 * Check if mouse button is released.
	 * 
	 * @param button {@code Integer}
	 * @return {@code Boolean}
	 */
	public boolean isMouseButtonReleased(int button) {
		return (!isMouseButtonDown(button) && mouseButtons[button]);
	}

	/**
	 * Get mouse position.
	 * 
	 * @return {@code Vector2f}
	 */
	public Vector2f getMousePosition() {
		glfwGetCursorPos(window, x, y);
		glfwGetWindowSize(window, winWidth, winHeight);

		mousePos.set((float) x[0] - (winWidth[0] / 2.0f), -((float) y[0] - (winHeight[0] / 2.0f)));

		return mousePos;
	}

	/**
	 * Update input.
	 */
	public void update() {
		for (int i = 32; i < GLFW_KEY_LAST; i++)
			keys[i] = isKeyDown(i);
		for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++)
			mouseButtons[i] = isMouseButtonDown(i);
	}
}
