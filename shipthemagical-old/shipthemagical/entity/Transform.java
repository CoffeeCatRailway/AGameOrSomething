package io.github.coffeecatrailway.shipthemagical.entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class Transform {
	
	public Vector3f pos;
	public Vector3f scale;
	
	/**
	 * Transform.java constructor.
	 */
	public Transform() {
		pos = new Vector3f();
		scale = new Vector3f(1, 1, 1);
	}
	
	/**
	 * Get projection.
	 * 
	 * @param target {@code Matrix4f}
	 * @return {@code Matrix4f}
	 */
	public Matrix4f getProjection(Matrix4f target) {
		target.translate(pos);
		target.scale(scale);
		return target;
	}
}
