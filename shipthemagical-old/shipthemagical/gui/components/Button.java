package io.github.coffeecatrailway.shipthemagical.gui.components;

import org.joml.Vector2f;

import io.github.coffeecatrailway.shipthemagical.assets.Assets;
import io.github.coffeecatrailway.shipthemagical.collision.Collision;
import io.github.coffeecatrailway.shipthemagical.gui.Component;
import io.github.coffeecatrailway.shipthemagical.io.Input;
import io.github.coffeecatrailway.shipthemagical.render.Camera;
import io.github.coffeecatrailway.shipthemagical.render.Shader;
import io.github.coffeecatrailway.shipthemagical.render.SpriteSheet;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class Button extends Component {

	public static final int STATE_IDLE = 0;
	public static final int STATE_SELECTED = 1;
	public static final int STATE_CLICKED = 2;

	public int selectedState;

	/**
	 * 
	 * Button.java constructor.
	 * 
	 * @param x {@code Float}
	 * @param y {@code Float}
	 * @param xs {@code Float}
	 * @param ys {@code Float}
	 */
	public Button(float x, float y, float xs, float ys) {
		super(x, y, xs, ys);
		
		selectedState = STATE_IDLE;
	}
	
	@Override
	public void update(Input input) {
		Collision data = bounding_box.getCollision(input.getMousePosition());

		if (data.isIntersecting) {
			selectedState = STATE_SELECTED;

			if (input.isMouseButtonDown(0)) {
				selectedState = STATE_CLICKED;
			}
		} else {
			selectedState = STATE_IDLE;
		}
	}

	@Override
	public void render(Camera camera, SpriteSheet sheet, Shader shader) {
		Vector2f position = bounding_box.getCenter(), scale = bounding_box.getHalfExtent();

		// Middle
		transform.identity().translate(position.x, position.y, 0).scale(scale.x, scale.y, 1);
		shader.setUniform("projection", camera.getProjection().mul(transform));
		switch (selectedState) {
		case STATE_SELECTED:
			sheet.bindTile(shader, 4, 1);
			break;
		case STATE_CLICKED:
			sheet.bindTile(shader, 7, 1);
			break;
		default:
			sheet.bindTile(shader, 1, 1);
			break;
		}
		Assets.getModel().render();

		renderSides(position, scale, camera, sheet, shader);
		renderCorners(position, scale, camera, sheet, shader);
	}

	/**
	 * Renders sides of component.
	 * 
	 * @param position {@code Vector2f}
	 * @param scale {@code Vector2f}
	 * @param camera {@code Camera}
	 * @param sheet {@code SpriteSheet}
	 * @param shader {@code Shader}
	 */
	private void renderSides(Vector2f position, Vector2f scale, Camera camera, SpriteSheet sheet, Shader shader) {
		// Top
		transform.identity().translate(position.x, position.y + scale.y - 16, 0).scale(scale.x, 16, 1);
		shader.setUniform("projection", camera.getProjection().mul(transform));
		switch (selectedState) {
		case STATE_SELECTED:
			sheet.bindTile(shader, 4, 0);
			break;
		case STATE_CLICKED:
			sheet.bindTile(shader, 7, 0);
			break;
		default:
			sheet.bindTile(shader, 1, 0);
			break;
		}
		Assets.getModel().render();

		// Bottom
		transform.identity().translate(position.x, position.y - scale.y + 16, 0).scale(scale.x, 16, 1);
		shader.setUniform("projection", camera.getProjection().mul(transform));
		switch (selectedState) {
		case STATE_SELECTED:
			sheet.bindTile(shader, 4, 2);
			break;
		case STATE_CLICKED:
			sheet.bindTile(shader, 7, 2);
			break;
		default:
			sheet.bindTile(shader, 1, 2);
			break;
		}
		Assets.getModel().render();

		// Left
		transform.identity().translate(position.x - scale.x + 16, position.y, 0).scale(16, scale.y, 1);
		shader.setUniform("projection", camera.getProjection().mul(transform));
		switch (selectedState) {
		case STATE_SELECTED:
			sheet.bindTile(shader, 3, 1);
			break;
		case STATE_CLICKED:
			sheet.bindTile(shader, 6, 1);
			break;
		default:
			sheet.bindTile(shader, 0, 1);
			break;
		}
		Assets.getModel().render();

		// Right
		transform.identity().translate(position.x + scale.x - 16, position.y, 0).scale(16, scale.y, 1);
		shader.setUniform("projection", camera.getProjection().mul(transform));
		switch (selectedState) {
		case STATE_SELECTED:
			sheet.bindTile(shader, 5, 1);
			break;
		case STATE_CLICKED:
			sheet.bindTile(shader, 8, 1);
			break;
		default:
			sheet.bindTile(shader, 2, 1);
			break;
		}
		Assets.getModel().render();
	}

	/**
	 * Renders corners of component.
	 * 
	 * @param position {@code Vector2f}
	 * @param scale {@code Vector2f}
	 * @param camera {@code Camera}
	 * @param sheet {@code SpriteSheet}
	 * @param shader {@code Shader}
	 */
	private void renderCorners(Vector2f position, Vector2f scale, Camera camera, SpriteSheet sheet, Shader shader) {
		// Top Left
		transform.identity().translate(position.x - scale.x + 16, position.y + scale.y - 16, 0).scale(16, 16, 1);
		shader.setUniform("projection", camera.getProjection().mul(transform));
		switch (selectedState) {
		case STATE_SELECTED:
			sheet.bindTile(shader, 3, 0);
			break;
		case STATE_CLICKED:
			sheet.bindTile(shader, 6, 0);
			break;
		default:
			sheet.bindTile(shader, 0, 0);
			break;
		}
		Assets.getModel().render();

		// Top Right
		transform.identity().translate(position.x + scale.x - 16, position.y + scale.y - 16, 0).scale(16, 16, 1);
		shader.setUniform("projection", camera.getProjection().mul(transform));
		switch (selectedState) {
		case STATE_SELECTED:
			sheet.bindTile(shader, 5, 0);
			break;
		case STATE_CLICKED:
			sheet.bindTile(shader, 8, 0);
			break;
		default:
			sheet.bindTile(shader, 2, 0);
			break;
		}
		Assets.getModel().render();

		// Bottom Left
		transform.identity().translate(position.x - scale.x + 16, position.y - scale.y + 16, 0).scale(16, 16, 1);
		shader.setUniform("projection", camera.getProjection().mul(transform));
		switch (selectedState) {
		case STATE_SELECTED:
			sheet.bindTile(shader, 3, 2);
			break;
		case STATE_CLICKED:
			sheet.bindTile(shader, 6, 2);
			break;
		default:
			sheet.bindTile(shader, 0, 2);
			break;
		}
		Assets.getModel().render();

		// Bottom Right
		transform.identity().translate(position.x + scale.x - 16, position.y - scale.y + 16, 0).scale(16, 16, 1);
		shader.setUniform("projection", camera.getProjection().mul(transform));
		switch (selectedState) {
		case STATE_SELECTED:
			sheet.bindTile(shader, 5, 2);
			break;
		case STATE_CLICKED:
			sheet.bindTile(shader, 8, 2);
			break;
		default:
			sheet.bindTile(shader, 2, 2);
			break;
		}
		Assets.getModel().render();
	}
}
