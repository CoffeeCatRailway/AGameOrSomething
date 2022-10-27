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
public class TileOre extends Tile {

	/**
	 * TileOre.java constructor.
	 * Can be solid.
	 * 
	 * @param texture {@code }
	 * @param isSolid {@code }
	 */
	public TileOre(String texture, boolean isSolid) {
		super(texture);
		setSolid(isSolid);
	}

	@Override
	public void update(Input input, Shader shader, World world, Camera cam) {
	}
}
