package io.github.coffeecatrailway.shipthemagical.entity.entities;

import org.joml.Vector2f;

import io.github.coffeecatrailway.shipthemagical.entity.Entity;
import io.github.coffeecatrailway.shipthemagical.entity.Transform;
import io.github.coffeecatrailway.shipthemagical.io.Config;
import io.github.coffeecatrailway.shipthemagical.io.Window;
import io.github.coffeecatrailway.shipthemagical.io.Config.PropertyType;
import io.github.coffeecatrailway.shipthemagical.render.Camera;
import io.github.coffeecatrailway.shipthemagical.world.World;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class EntityTest extends Entity {

	public static final int ANIM_MAIN = 0;
	public static final int ANIM_SIZE = 1;

	/**
	 * The different types of test entities.
	 */
	public static enum TestEntityType {
		FOLLOW_P, FOLLOW_T
	}

	private TestEntityType type;
	private Config properties;

	/**
	 * TestEntity.java constructor.
	 * 
	 * @param transform
	 *            {@code Transform}
	 * @param hasEntityCollision
	 *            {@code Boolean}
	 */
	public EntityTest(Transform transform, boolean hasEntityCollision, TestEntityType type) {
		super("test_entity", ANIM_SIZE, transform, hasEntityCollision, false);
		String entity_type = type.toString().toLowerCase();
		this.type = type;

		properties = new Config(path + entity_type + "/entity.properties", Config.RES);

		int frame_count = (int) properties.getProperty("test_entity.animation.frame_count", PropertyType.INT);
		setAnimation(ANIM_MAIN, frame_count, 2, "test_entity/" + entity_type);
	}

	@Override
	public void update(float delta, Window window, Camera camera, World world) {
		Vector2f movement = new Vector2f();

		float x = 0, y = 0;
		switch (type) {
		case FOLLOW_P:
			x = world.player.getPos().x - this.getPos().x;
			y = world.player.getPos().y - this.getPos().y;
			break;
		case FOLLOW_T:
			x = world.test_p.getPos().x - this.getPos().x;
			y = world.test_p.getPos().y - this.getPos().y;
			break;
		}

		float distance = (float) Math.sqrt(x * x + y * y);
		float speed = (float) properties.getProperty("test_entity.speed", PropertyType.FLOAT);
		// * (float) Math.sqrt(distance / 2);
		float multiplier = speed / distance;

		float mx = x * multiplier;
		float my = y * multiplier;

		if (distance > 4 && distance < 20) {
			movement.add(mx * delta, my * delta);
		}

		move(movement);
	}
}
