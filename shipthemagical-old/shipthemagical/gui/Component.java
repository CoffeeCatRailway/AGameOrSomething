package io.github.coffeecatrailway.shipthemagical.gui;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import io.github.coffeecatrailway.shipthemagical.collision.AABB;
import io.github.coffeecatrailway.shipthemagical.io.Input;
import io.github.coffeecatrailway.shipthemagical.render.Camera;
import io.github.coffeecatrailway.shipthemagical.render.Shader;
import io.github.coffeecatrailway.shipthemagical.render.SpriteSheet;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.gui
 */
public abstract class Component {
	
	protected AABB bounding_box;
	protected Matrix4f transform = new Matrix4f();

	/**
	 * Component.java constructor.
	 * 
	 * @param x {@code Float}
	 * @param y {@code Float}
	 * @param xs {@code Float}
	 * @param ys {@code Float}
	 */
	public Component(float x, float y, float xs, float ys) {
		Vector2f position = new Vector2f(x, y);
		Vector2f scale = new Vector2f(xs, ys);
		bounding_box = new AABB(position, scale);
	}
	
	/**
	 * Sets position of component.
	 * 
	 * @param x {@code Float}
	 * @param y {@code Float}
	 * @param xs {@code Float}
	 * @param ys {@code Float}
	 */
	public void setPosition(float x, float y, float xs, float ys) {
		Vector2f position = new Vector2f(x, y);
		Vector2f scale = new Vector2f(xs, ys);
		bounding_box = new AABB(position, scale);
	}
	
	/**
	 * Gets the position of component.
	 * 
	 * @return {@code Vector2f}
	 */
	public Vector2f getPosition() {
		return bounding_box.getCenter();
	}
	
	/**
	 * Updates component.
	 * 
	 * @param input {@code Input}
	 */
	public abstract void update(Input input);
	
	/**
	 * Renders component.
	 * 
	 * @param camera {@code Camera}
	 * @param sheet {@code SpriteSheet}
	 * @param shader {@code Shader}
	 */
	public abstract void render(Camera camera, SpriteSheet sheet, Shader shader);
}
