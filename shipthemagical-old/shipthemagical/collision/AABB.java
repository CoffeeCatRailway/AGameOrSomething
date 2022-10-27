package io.github.coffeecatrailway.shipthemagical.collision;

import org.joml.Vector2f;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class AABB {

	private Vector2f center, half_extent;

	/**
	 * AABB.java constructor.
	 * 
	 * @param  center {@code Vector2f}
     * @param  half_extent {@code Vector2f}
	 */
	public AABB(Vector2f center, Vector2f half_extent) {
		this.center = center;
		this.half_extent = half_extent;
	}

	/**
	 * Gets the collision of two objects.
	 * 
	 * @param  box {@code AABB}
     * @return  the collision of the box object and this.
	 */
	public Collision getCollision(AABB box) {
		Vector2f distance = box.center.sub(center, new Vector2f());
		distance.x = Math.abs(distance.x);
		distance.y = Math.abs(distance.y);

		distance.sub(half_extent.add(box.half_extent, new Vector2f()));

		return new Collision(distance, distance.x < 0 && distance.y < 0);
	}

	/**
	 * Gets the collision of two objects.
	 * 
	 * @param  point {@code Vector2f}
     * @return  the collision of the box object and this.
	 */
	public Collision getCollision(Vector2f point) {
		Vector2f distance = point.sub(center);
		distance.x = Math.abs(distance.x);
		distance.y = Math.abs(distance.y);

		distance.sub(half_extent);

		return new Collision(distance, distance.x < 0 && distance.y < 0);
	}

	/**
	 * Corrects position of the box parameter
	 * 
	 * @param  box {@code AABB}
     * @param  data {@code Collision}
	 */
	public void correctPosition(AABB box, Collision data) {
		Vector2f correction = box.center.sub(center, new Vector2f());
		if (data.distance.x > data.distance.y) {
			if (correction.x > 0) {
				center.add(data.distance.x, 0);
			} else {
				center.add(-data.distance.x, 0);
			}
		} else {
			if (correction.y > 0) {
				center.add(0, data.distance.y);
			} else {
				center.add(0, -data.distance.y);
			}
		}
	}

	/**
	 * @return  the center ({@code Vector2f}) and this object.
	 */
	public Vector2f getCenter() {
		return center;
	}

	/**
	 * @return  the half extent ({@code Vector2f}) and this object.
	 */
	public Vector2f getHalfExtent() {
		return half_extent;
	}
}
