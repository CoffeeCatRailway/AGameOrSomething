package io.github.coffeecatrailway.shipthemagical.world.tiles.multi;

import io.github.coffeecatrailway.shipthemagical.io.Input;
import io.github.coffeecatrailway.shipthemagical.render.Camera;
import io.github.coffeecatrailway.shipthemagical.render.Shader;
import io.github.coffeecatrailway.shipthemagical.world.Tile;
import io.github.coffeecatrailway.shipthemagical.world.World;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class TileClay extends Tile {

	/**
	 * TileClay.java constructor. Can be solid.
	 * 
	 * @param texture
	 *            {@code }
	 * @param isSolid
	 *            {@code }
	 */
	public TileClay(String texture, boolean isSolid) {
		super(texture);
		setSolid(isSolid);
	}

	@Override
	public void update(Input input, Shader shader, World world, Camera cam) {
//		Collision data = world.player.getBoundingBox().getCollision(world.elder_red.getBoundingBox());
//
//		if (data.isIntersecting) {
//			setTexture("hardened_clay");
//			setSolid(true);
//			world.setTile(Tile.hardened_clay, (int) world.player.getPos().x, (int) world.player.getPos().y);
//		}
	}
}
