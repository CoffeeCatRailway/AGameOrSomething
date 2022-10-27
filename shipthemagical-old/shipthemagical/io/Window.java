package io.github.coffeecatrailway.shipthemagical.io;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class Window {

	private long window;

	private int width, height;
	private boolean fullscreen;
	private boolean hasResized;
	private GLFWWindowSizeCallback windowSizeCallbacl;

	private Input input;

	/**
	 * Set error call backs.
	 */
	public static void setCallbacks() {
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
	}

	/**
	 * Set local call backs.
	 */
	private void setLocalCallbacks() {
		windowSizeCallbacl = new GLFWWindowSizeCallback() {

			@Override
			public void invoke(long argWindow, int argWidth, int argHeight) {
				width = argWidth;
				height = argHeight;
				hasResized = true;
			}
		};

		glfwSetWindowSizeCallback(window, windowSizeCallbacl);
	}

	/**
	 * Window.java constructor.
	 */
	public Window() {
		fullscreen = false;
		hasResized = false;

		setSize(640, 480);
		setFullscreen(fullscreen);
	}

	/**
	 * Clean up the window.
	 */
	public void cleanUp() {
		glfwFreeCallbacks(window);
	}

	/**
	 * Create the window.
	 * 
	 * @param title
	 *            {@code String}
	 */
	public void createWindow(String title) {
		window = glfwCreateWindow(width, height, title, fullscreen ? glfwGetPrimaryMonitor() : 0, 0);

		if (window == 0) {
			throw new IllegalStateException("Failed to create window!");
		}

		if (!fullscreen) {
			GLFWVidMode video = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(window, (video.width() - width) / 2, (video.height() - height) / 2);
		}
		glfwShowWindow(window);
		glfwMakeContextCurrent(window);

		input = new Input(window);
		setLocalCallbacks();
	}

	/**
	 * Set window icon.
	 * 
	 * @param window
	 *            {@code Long}
	 * @param img
	 *            {@code BufferedImage}
	 */
	public void setIcon(long window, BufferedImage img) {
		GLFWImage image = GLFWImage.malloc();
		image.set(img.getWidth(), img.getHeight(), loadImageToByteBuffer(img));

		GLFWImage.Buffer images = GLFWImage.malloc(1);
		images.put(0, image);

		GLFW.glfwSetWindowIcon(window, images);

		images.free();
		image.free();
	}

	/**
	 * Set window icon.
	 * Max of 2 images!
	 * 
	 * @param window
	 *            {@code Long}
	 * @param imgs
	 *            {@code BufferedImage[]}
	 */
	public void setIcon(long window, BufferedImage[] imgs) {
		GLFWImage image = null;
		GLFWImage.Buffer images = null;

		for (int i = 0; i < imgs.length-1; i++) {
			image = GLFWImage.malloc();
			image.set(imgs[i].getWidth(), imgs[i].getHeight(), loadImageToByteBuffer(imgs[i]));

			images = GLFWImage.malloc(1);
			images.put(i, image);
		}

		GLFW.glfwSetWindowIcon(window, images);

		images.free();
		image.free();
	}

	/**
	 * @param image
	 *            {@code final BufferedImage}
	 * @return {@code ByteBuffer}
	 */
	private ByteBuffer loadImageToByteBuffer(final BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		int[] pixels_raw = new int[width * height * 4];
		pixels_raw = image.getRGB(0, 0, width, height, null, 0, width);

		ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int pixel = pixels_raw[i * width + j];

				pixels.put((byte) ((pixel >> 16) & 0xFF)); // Red
				pixels.put((byte) ((pixel >> 8) & 0xFF)); // Green
				pixels.put((byte) (pixel & 0xFF)); // Blue
				pixels.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
			}
		}
		pixels.flip();
		return pixels;
	}

	/**
	 * @return {@code Boolean}
	 */
	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}

	public void swapBuffers() {
		glfwSwapBuffers(window);
	}

	/**
	 * Set size of window.
	 * 
	 * @param width
	 *            {@code Integer}
	 * @param height
	 *            {@code Integer}
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Set window to fullscreen.
	 * 
	 * @param fullscreen
	 *            {@code Boolean}
	 */
	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	/**
	 * Update window.
	 */
	public void update() {
		hasResized = false;
		input.update();
		glfwPollEvents();
	}

	/**
	 * Get if window width.
	 * 
	 * @return {@code Integer}
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get if window height.
	 * 
	 * @return {@code Integer}
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get if window if is fullscreen.
	 * 
	 * @return {@code Boolean}
	 */
	public boolean hasResized() {
		return hasResized;
	}

	/**
	 * Get if window if is fullscreen.
	 * 
	 * @return {@code Boolean}
	 */
	public boolean getFullscreen() {
		return fullscreen;
	}

	/**
	 * Get window.
	 * 
	 * @return {@code Long}
	 */
	public long getWindow() {
		return window;
	}

	/**
	 * Get window input.
	 * 
	 * @return {@code Input}
	 */
	public Input getInput() {
		return input;
	}
}
