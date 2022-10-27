package io.github.coffeecatrailway.shipthemagical.render;

import io.github.coffeecatrailway.shipthemagical.io.Timer;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class Animation {

	private Texture[] frames;
	private int pointer;

	private double elapsedTime;
	private double currentTime;
	private double lastTime;
	private double fps;

	/**
	 * Animation.java constructor.
	 * 
	 * @param amount {@code Integer}
	 * @param fps {@code Integer}
	 * @param filename {@code String}
	 */
	public Animation(int amount, int fps, String filename) {
		pointer = 0;
		elapsedTime = 0;
		currentTime = 0;
		lastTime = Timer.getTime();
		this.fps = 1.0 / (double) fps;

		frames = new Texture[amount];
		for (int i = 0; i < amount; i++) {
			frames[i] = new Texture(filename+"/frame_" + i + ".png");
		}
	}

	/**
	 * Bind animation.
	 */
	public void bind() {
		bind(0);
	}

	/**
	 * Bind animation.
	 * 
	 * @param sampler {@code Integer}
	 */
	public void bind(int sampler) {
		currentTime = Timer.getTime();
		elapsedTime += currentTime - lastTime;

		if (elapsedTime >= fps) {
			elapsedTime = 0;
			pointer++;
		}

		if (pointer >= frames.length)
			pointer = 0;
		lastTime = currentTime;
		frames[pointer].bind(sampler);
	}
}
