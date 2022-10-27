package io.github.coffeecatrailway.shipthemagical.gui;

import io.github.coffeecatrailway.shipthemagical.io.Input;
import io.github.coffeecatrailway.shipthemagical.io.Window;
import io.github.coffeecatrailway.shipthemagical.render.Camera;
import io.github.coffeecatrailway.shipthemagical.render.Shader;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public abstract class Gui {

	public static Shader shader;
	public static Camera camera;
	
	/**
	 * Gui.java constructor.
	 * 
	 * @param window {@code Window}
	 * @param shaderFile {@code String}
	 */
	public Gui(Window window, String shaderFile) {
		shader = new Shader("guis/"+shaderFile);
		camera = new Camera(window.getWidth(), window.getHeight());
	}

	/**
	 * Resizes camera.
	 * 
	 * @param window {@code }
	 */
	public void resizeCamera(Window window) {
		camera.setProjection(window.getWidth(), window.getHeight());
	}
	
	/**
	 * Updates gui.
	 * 
	 * @param input {@code }
	 */
	public abstract void update(Input input);

	/**
	 * Renders gui.
	 * 
	 *  {@code }
	 */
	public void render() {
		shader.bind();
	}
}
