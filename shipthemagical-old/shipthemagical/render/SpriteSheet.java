package io.github.coffeecatrailway.shipthemagical.render;

import org.joml.Matrix4f;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class SpriteSheet {

	private Texture texture;
	
	private Matrix4f scale;
	private Matrix4f translation;
	
	private int tileAmount;
	
	/**
	 * SpriteSheet.java constructor.
	 * 
	 * @param texture {@code String}
	 * @param tileAmount {@code Integer}
	 */
	public SpriteSheet(String texture, int tileAmount) {
		this.texture = new Texture("spritesheets/"+texture+".png");
		
		scale = new Matrix4f().scale(1.0f/(float)tileAmount);
		translation = new Matrix4f();
		this.tileAmount = tileAmount;
	}
	
	/**
	 * Bind tile from sheet.
	 * 
	 * @param shader {@code Shader}
	 * @param x {@code Integer}
	 * @param y {@code Integer}
	 */
	public void bindTile(Shader shader, int x, int y) {
		scale.translate(x, y, 0, translation);
		
		shader.setUniform("sampler", 0);
		shader.setUniform("texModifier", translation);
		texture.bind(0);
	}
	
	/**
	 * Bind tile from sheet.
	 * 
	 * @param shader {@code Shader}
	 * @param tile {@code Integer}
	 */
	public void bindTile(Shader shader, int tile) {
		int x = tile % tileAmount;
		int y = tile / tileAmount;
		bindTile(shader, x, y);
	}
}
