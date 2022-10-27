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
public class EntityCoin extends Entity {

	public static final int ANIM_MAIN = 0;
	public static final int ANIM_SIZE = 1;

	/**
	 * The different coin types.
	 */
	public static enum CoinType {
		GOLD, COPPER, SILVER
	}
	
	private CoinType type;

	/**
	 * Coin.java constructor.
	 * 
	 * @param transform {@code Transform}
	 * @param hasEntityCollision {@code Boolean}
	 * @param type {@code CoinType}
	 */
	public EntityCoin(Transform transform, boolean hasEntityCollision, CoinType type) {
		super("coin", ANIM_SIZE, transform, hasEntityCollision, false);
		String coin_type = type.toString().toLowerCase();
		this.type = type;

		Config properties = new Config(path + coin_type + "/entity.properties", Config.RES);
		int frame_count = (int) properties.getProperty("coin.animation.frame_count", PropertyType.INT);

		setAnimation(ANIM_MAIN, frame_count, 10, "coin/" + coin_type);
	}

	@Override
	public void update(float delta, Window window, Camera camera, World world) {
		switch (type) {
		case COPPER:
			break;
		case GOLD:
			break;
		case SILVER:
			break;
		}
	}
}
