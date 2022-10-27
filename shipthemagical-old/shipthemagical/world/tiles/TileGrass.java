package io.github.coffeecatrailway.shipthemagical.world.tiles;

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
public class TileGrass extends Tile {

	/**
	 * TileGrass.java constructor.
	 * Is not solid.
	 * 
	 * @param texture {@code String}
	 */
	public TileGrass(String texture) {
		super(texture);
	}

	@Override
	public void update(Input input, Shader shader, World world, Camera cam) {
	}
}
