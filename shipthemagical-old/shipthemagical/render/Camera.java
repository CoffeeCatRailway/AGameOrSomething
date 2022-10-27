package io.github.coffeecatrailway.shipthemagical.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class Camera {

	private Vector3f position;
	private Matrix4f projection;

	/**
	 * Camera.java constructor.
	 * 
	 * @param width {@code Integer}
	 * @param height {@code Integer}
	 */
	public Camera(int width, int height) {
		position = new Vector3f(0, 0, 0);
		setProjection(width, height);
	}
	
	/**
	 * Set camera projection.
	 * 
	 * @param width {@code }
	 * @param height {@code }
	 */
	public void setProjection(int width, int height) {
		projection = new Matrix4f().setOrtho2D(-width/2, width/2, -height/2, height/2);
	}
	
	/**
	 * Set camera position.
	 * 
	 * @param position {@code Vector3f}
	 */
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	/**
	 * Add to camera position.
	 * 
	 * @param position {@code Vector3f}
	 */
	public void addPosition(Vector3f position) {
		this.position.add(position);
	}
	
	/**
	 * Get camera position.
	 * 
	 * @return {@code Vector3f}
	 */
	public Vector3f getPosition() {
		return position;
	}
	
	/**
	 * @return {@code Matrix4f}
	 */
	public Matrix4f getUntransformedProjection() {
		return projection;
	}
	
	/**
	 * Get camera projection.
	 * 
	 * @return {@code Matrix4f}
	 */
	public Matrix4f getProjection() {
		return projection.translate(position, new Matrix4f());
	}
}
