package io.github.coffeecatrailway.shipthemagical.entity;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import io.github.coffeecatrailway.shipthemagical.assets.Assets;
import io.github.coffeecatrailway.shipthemagical.assets.PlayerAssets;
import io.github.coffeecatrailway.shipthemagical.collision.AABB;
import io.github.coffeecatrailway.shipthemagical.collision.Collision;
import io.github.coffeecatrailway.shipthemagical.io.Window;
import io.github.coffeecatrailway.shipthemagical.render.Animation;
import io.github.coffeecatrailway.shipthemagical.render.Camera;
import io.github.coffeecatrailway.shipthemagical.render.Shader;
import io.github.coffeecatrailway.shipthemagical.world.World;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public abstract class Entity {

	protected AABB bounding_box;

	protected Animation[] animations;
	protected Transform transform;

	protected String path = "textures/entities/";

	private int use_animation;
	private boolean hasEntityCollision;
	private boolean isPlayer;

	/**
	 * Entity.java constructor.
	 * 
	 * @param max_animations
	 *            {@code Integer}
	 * @param transform
	 *            {@code Transform}
	 * @param hasEntityCollision
	 *            {@code Boolean}
	 * @param isPlayer
	 *            {@code Boolean}
	 */
	public Entity(String name, int max_animations, Transform transform, boolean hasEntityCollision, boolean isPlayer) {
		animations = new Animation[max_animations];
		use_animation = 0;
		
		path += name+"/";

		this.transform = transform;
		this.hasEntityCollision = hasEntityCollision;
		this.isPlayer = isPlayer;

		bounding_box = new AABB(new Vector2f(transform.pos.x, transform.pos.y),
				new Vector2f(transform.scale.x, transform.scale.y));
	}

	/**
	 * Updates entity.
	 * 
	 * @param delta
	 *            {@code Float}
	 * @param window
	 *            {@code Window}
	 * @param camera
	 *            {@code Camera}
	 * @param world
	 *            {@code World}
	 */
	public abstract void update(float delta, Window window, Camera camera, World world);

	/**
	 * @return the path {@code String}
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the entity animation.
	 * 
	 * @param index
	 *            {@code Integer}
	 * @param animation
	 *            {@code Animation}
	 */
	protected void setAnimation(int index, Animation animation) {
		animations[index] = animation;
	}

	/**
	 * Sets the entity animation.
	 * 
	 * @param index
	 *            {@code Integer}
	 * @param amount
	 *            {@code Integer}
	 * @param fps
	 *            {@code Integer}
	 * @param filename
	 *            {@code String}
	 */
	protected void setAnimation(int index, int amount, int fps, String filename) {
		animations[index] = new Animation(amount, fps, "entities/" + filename);
	}

	/**
	 * Selectes what animation to use.
	 * 
	 * @param index
	 *            {@code Integer}
	 */
	public void useAnimation(int index) {
		use_animation = index;
	}

	/**
	 * Get entity position.
	 * 
	 * @return {@code Vector3f}
	 */
	public Vector3f getPos() {
		return transform.pos;
	}

	/**
	 * Get entity bounding box
	 * 
	 * @return {@code AABB}
	 */
	public AABB getBoundingBox() {
		return bounding_box;
	}

	/**
	 * Check if entity has entity collision.
	 * 
	 * @return {@code Boolean}
	 */
	public boolean hasEntityCollision() {
		return hasEntityCollision;
	}

	/**
	 * Moves entity.
	 * 
	 * @param direction
	 *            {@code Vector2f}
	 */
	public void move(Vector2f direction) {
		transform.pos.add(new Vector3f(direction, 0));

		bounding_box.getCenter().set(transform.pos.x, transform.pos.y);
	}

	/**
	 * Collide with tiles.
	 * 
	 * @param world
	 *            {@code World}
	 */
	public void collideWithTiles(World world) {
		AABB[] boxes = new AABB[25];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				boxes[i + j * 5] = world.getTileBoundingBox((int) (((transform.pos.x / 2) + 0.5f) - (5 / 2)) + i,
						(int) (((-transform.pos.y / 2) + 0.5f) - (5 / 2)) + j);
			}
		}

		AABB box = null;
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i] != null) {
				if (box == null)
					box = boxes[i];
				Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
				Vector2f length2 = boxes[i].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());

				if (length1.lengthSquared() > length2.lengthSquared()) {
					box = boxes[i];
				}
			}
		}

		if (box != null) {
			Collision data = bounding_box.getCollision(box);
			if (data.isIntersecting) {
				bounding_box.correctPosition(box, data);
				transform.pos.x = bounding_box.getCenter().x;
				transform.pos.y = bounding_box.getCenter().y;
			}

			for (int i = 0; i < boxes.length; i++) {
				if (boxes[i] != null) {
					if (box == null)
						box = boxes[i];
					Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
					Vector2f length2 = boxes[i].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());

					if (length1.lengthSquared() > length2.lengthSquared()) {
						box = boxes[i];
					}
				}
			}

			data = bounding_box.getCollision(box);
			if (data.isIntersecting) {
				bounding_box.correctPosition(box, data);
				transform.pos.x = bounding_box.getCenter().x;
				transform.pos.y = bounding_box.getCenter().y;
			}
		}
	}

	/**
	 * Collide with entities.
	 * 
	 * @param entity
	 *            {@code Entity}
	 */
	public void collideWithEntity(Entity entity) {
		if (entity.hasEntityCollision()) {
			Collision collision = bounding_box.getCollision(entity.bounding_box);

			if (collision.isIntersecting) {
				collision.distance.x /= 2;
				collision.distance.y /= 2;

				bounding_box.correctPosition(entity.bounding_box, collision);
				transform.pos.set(bounding_box.getCenter().x, bounding_box.getCenter().y, 0);

				entity.bounding_box.correctPosition(bounding_box, collision);
				entity.transform.pos.set(entity.bounding_box.getCenter().x, entity.bounding_box.getCenter().y, 0);
			}
		}
	}

	/**
	 * Render entity.
	 * 
	 * @param shader
	 *            {@code Shader}
	 * @param camera
	 *            {@code Camera}
	 * @param world
	 *            {@code World}
	 */
	public void render(Shader shader, Camera camera, World world) {
		Matrix4f target = camera.getProjection();
		target.mul(world.getWorldMatrix());

		shader.bind();
		shader.setUniform("sampler", 0);
		shader.setUniform("projection", transform.getProjection(target));
		animations[use_animation].bind();
		if (isPlayer)
			PlayerAssets.getModel().render();
		else
			Assets.getModel().render();
	}
}
