package io.github.coffeecatrailway.shipthemagical.entity.entities;

import io.github.coffeecatrailway.shipthemagical.entity.Entity;
import io.github.coffeecatrailway.shipthemagical.entity.Transform;
import io.github.coffeecatrailway.shipthemagical.io.Config;
import io.github.coffeecatrailway.shipthemagical.io.Config.PropertyType;
import io.github.coffeecatrailway.shipthemagical.io.Window;
import io.github.coffeecatrailway.shipthemagical.render.Camera;
import io.github.coffeecatrailway.shipthemagical.world.World;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class EntityElder extends Entity {

	public static final int ANIM_MAIN = 0;
	public static final int ANIM_SIZE = 1;

	/**
	 * The different types of elders.
	 */
	public static enum ElderType {
		FIRE, WATER, EARTH, AIR
	}

	private ElderType type;

	/**
	 * Elder.java constructor.
	 * 
	 * @param transform {@code Transform}
	 * @param hasEntityCollision {@code Boolean}
	 * @param type {@code ElderType}
	 */
	public EntityElder(Transform transform, boolean hasEntityCollision, ElderType type) {
		super("elder", ANIM_SIZE, transform, hasEntityCollision, false);
		String elder_type = type.toString().toLowerCase();
		this.type = type;

		Config properties = new Config(path + elder_type + "/entity.properties", Config.RES);
		int frame_count = (int) properties.getProperty("elder.animation.frame_count", PropertyType.INT);

		setAnimation(ANIM_MAIN, frame_count, 10, "elder/" + elder_type);
	}

	@Override
	public void update(float delta, Window window, Camera camera, World world) {
		switch (type) {
		case FIRE:
			break;
		case WATER:
			break;
		case EARTH:
			break;
		case AIR:
			break;
		default:
			break;
		}
	}
}
