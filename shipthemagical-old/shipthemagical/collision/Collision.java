package io.github.coffeecatrailway.shipthemagical.collision;

import org.joml.Vector2f;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class Collision {
	
	public Vector2f distance;
	public boolean isIntersecting;
	
	/**
	 * Collision.java constructor.
	 * 
	 * @param  distance {@code Vector2f}
     * @param  intersects {@code boolean}
	 */
	public Collision(Vector2f distance, boolean intersects) {
		this.distance = distance;
		isIntersecting = intersects;
	}
}
