package io.github.coffeecatrailway.shipthemagical.gui.guis;

import static org.lwjgl.glfw.GLFW.*;

import io.github.coffeecatrailway.shipthemagical.gui.Gui;
import io.github.coffeecatrailway.shipthemagical.gui.components.Button;
import io.github.coffeecatrailway.shipthemagical.gui.components.sheetwords.Quit;
import io.github.coffeecatrailway.shipthemagical.gui.components.sheetwords.Start;
import io.github.coffeecatrailway.shipthemagical.gui.components.sheetwords.Title;
import io.github.coffeecatrailway.shipthemagical.io.Config;
import io.github.coffeecatrailway.shipthemagical.io.Input;
import io.github.coffeecatrailway.shipthemagical.io.Window;
import io.github.coffeecatrailway.shipthemagical.render.SpriteSheet;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class GuiMainMenu extends Gui {

	public Config config;

	private Window window;
	private SpriteSheet sheet;

	private Title title;

	private Button start;
	private Start start_word;

	private Button quit;
	private Quit quit_word;

	private float x_offset = 0;
	private float y_offset = 100;

	/**
	 * GuiMainMenu.java constructor.
	 * 
	 * @param window {@code Window}
	 */
	public GuiMainMenu(Window window) {
		super(window, "gui_main_menu");
		this.window = window;
		config = new Config("game.config", Config.RES);
		sheet = new SpriteSheet("gui", 9);

		float width_title = 32;
		float height_title = 32;
		float title_x = -(window.getWidth() / 2) + (window.getWidth() / 3);
		float title_y = 0;
		title = new Title(title_x + x_offset, title_y + y_offset, width_title, height_title);

		// Start Button
		float button_width = 72;
		float button_height = 32;
		float resume_x = 0;
		float resume_y = -(button_height * 2) - button_height;
		start = new Button(resume_x + x_offset, resume_y + y_offset, button_width, button_height);
		
		float start_word_width = 30;
		float start_word_height = 27;
		float start_word_x = (-button_width / 2) + 30;
		float start_word_y = (-(start_word_height * 2) - start_word_height)-10;
		start_word = new Start(start_word_x + x_offset, start_word_y + y_offset, start_word_width, start_word_height);

		// Quit Button
		float quit_x = 0;
		float quit_y = (-(button_height * 2) - button_height) * 2;
		quit = new Button(quit_x + x_offset, quit_y + y_offset, button_width, button_height);

		float quit_word_width = 30;
		float quit_word_height = 27;
		float quit_word_x = (-button_width / 2) + 30;
		float quit_word_y = ((-(quit_word_height * 2) - quit_word_height) + 5) * 2.5f;
		quit_word = new Quit(quit_word_x + x_offset, quit_word_y + y_offset, quit_word_width, quit_word_height);
	}

	@Override
	public void update(Input input) {
		start.update(input);
		quit.update(input);
		
		if (start.selectedState == 2) {
			
		}
		
		if (quit.selectedState == 2) {
			// System.out.println("Button click");
			glfwSetWindowShouldClose(window.getWindow(), true);
		}
	}

	@Override
	public void render() {
		super.render();
		title.render(camera, sheet, shader);

		start.render(camera, sheet, shader);
		start_word.render(camera, sheet, shader);
		
		quit.render(camera, sheet, shader);
		quit_word.render(camera, sheet, shader);
	}
}
