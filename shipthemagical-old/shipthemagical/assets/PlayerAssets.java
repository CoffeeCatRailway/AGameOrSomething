package io.github.coffeecatrailway.shipthemagical.assets;

import io.github.coffeecatrailway.shipthemagical.io.Config;
import io.github.coffeecatrailway.shipthemagical.io.Config.PropertyType;
import io.github.coffeecatrailway.shipthemagical.render.Model;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class PlayerAssets {

	private static Model model;

	private static float[] vertices;
	private static float[] texture;
	private static int[] indices;

	private String path = "textures/entities/player/player.config";
	
	/**
	 * PlayerAssets.java constructor.
	 */
	public PlayerAssets() {
		Config config = new Config(path, Config.RES);
		float max;
		if ((boolean) config.getProperty("player.size.hasCap", PropertyType.BOOLEAN)) {
			max = 100f;
		} else {
			max = 6f;
		}

		float scale = (float) config.getProperty("player.size", PropertyType.FLOAT);
		if (scale > max) scale = max;

		float[] vertices = new float[] {
				-scale, scale, 0,	// TOP LEFT 0
				scale, scale, 0,	// TOP RIGHT 1
				scale, -scale, 0,	// BOTTOM RIGHT 2
				-scale, -scale, 0,	// BOTTOM LEFT 3
		};

		float[] texture = new float[] {
				0, 0,
				1, 0,
				1, 1,
				0, 1
		};

		int[] indices = new int[] {
				0, 1, 2,
				2, 3, 0
		};

		PlayerAssets.vertices = vertices;
		PlayerAssets.texture = texture;
		PlayerAssets.indices = indices;
	}
	
	/**
	 * Gets player model.
	 * 
	 * @return {@code Model}
	 */
	public static Model getModel() {
		return model;
	}

	/**
	 * Initializes the player model.
	 */
	public void initAsset() {
		model = new Model(vertices, texture, indices);
	}

	/**
	 * Deletes the player model.
	 */
	public void deleteAsset() {
		model = null;
	}
}
