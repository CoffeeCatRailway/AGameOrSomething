package io.github.coffeecatrailway.shipthemagical.world.tiles.solid;

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
public class TileLog extends Tile {

	/**
	 * TileLog.java constructor.
	 * Is solid.
	 * 
	 * @param texture {@code String}
	 */
	public TileLog(String texture) {
		super(texture);
		setSolid(true);
	}

	@Override
	public void update(Input input, Shader shader, World world, Camera cam) {
	}
}
