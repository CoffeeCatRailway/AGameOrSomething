package io.github.coffeecatrailway.shipthemagical.gui.components.sheetwords;

import org.joml.Vector2f;

import io.github.coffeecatrailway.shipthemagical.assets.Assets;
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
public class Resume extends Component {

	/**
	 * 
	 * Resume.java constructor.
	 * 
	 * @param x {@code Float}
	 * @param y {@code Float}
	 * @param xs {@code Float}
	 * @param ys {@code Float}
	 */
	public Resume(float x, float y, float xs, float ys) {
		super(x, y, xs, ys);
	}

	@Override
	public void update(Input input) {
	}

	@Override
	public void render(Camera camera, SpriteSheet sheet, Shader shader) {
		Vector2f position = bounding_box.getCenter(), scale = bounding_box.getHalfExtent();

		float xs = scale.x;
		float ys = scale.y;

		float offset = 0;
		int r = 4;

		transform.identity().translate(position.x - (xs + offset), position.y, 0).scale(xs, ys, 1);
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 6, r);
		Assets.getModel().render();

		transform.identity().translate(position.x + (xs - offset) * 1, position.y, 0).scale(xs, ys, 1);
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 7, r);
		Assets.getModel().render();
		
		transform.identity().translate(position.x + (xs - offset) * 3, position.y, 0).scale(xs, ys, 1);
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 8, r);
		Assets.getModel().render();
	}
}
