package io.github.coffeecatrailway.shipthemagical.assets;

import io.github.coffeecatrailway.shipthemagical.render.Model;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class Assets {
	
	private static Model model;
	
	/**
	 * Gets model;
	 * 
	 * @return {@code Model}
	 */
	public static Model getModel() {
		return model;
	}
	
	/**
	 * Initializes the model.
	 */
	public static void initAsset() {
		float[] vertices = new float[] {
				-1f, 1f, 0, //TOP LEFT     0
				1f, 1f, 0,  //TOP RIGHT    1
				1f, -1f, 0, //BOTTOM RIGHT 2
				-1f, -1f, 0,//BOTTOM LEFT  3  
		};
		
		float[] texture = new float[] {
				0,0,
				1,0,
				1,1,
				0,1,
		};
		
		int[] indices = new int[] {
				0,1,2,
				2,3,0
		};
		
		model = new Model(vertices, texture, indices);
	}
	
	/**
	 * Deletes the model.
	 */
	public static void deleteAsset() {
		model = null;
	}
}
