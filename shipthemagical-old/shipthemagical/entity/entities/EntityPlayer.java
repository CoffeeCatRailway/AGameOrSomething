package io.github.coffeecatrailway.shipthemagical.entity.entities;

import static org.lwjgl.glfw.GLFW.*;

import java.io.IOException;

import org.joml.Vector2f;
import org.joml.Vector3f;

import io.github.coffeecatrailway.shipthemagical.collision.AABB;
import io.github.coffeecatrailway.shipthemagical.entity.Entity;
import io.github.coffeecatrailway.shipthemagical.entity.Transform;
import io.github.coffeecatrailway.shipthemagical.io.Config;
import io.github.coffeecatrailway.shipthemagical.io.Config.PropertyType;
import io.github.coffeecatrailway.shipthemagical.io.Input;
import io.github.coffeecatrailway.shipthemagical.io.Window;
import io.github.coffeecatrailway.shipthemagical.render.Camera;
import io.github.coffeecatrailway.shipthemagical.world.World;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class EntityPlayer extends Entity {

	public static final int ANIM_IDLE = 0;
	public static final int ANIM_WALK = 1;
	public static final int ANIM_WALK_DOWN = 2;
	public static final int ANIM_WALK_UP = 3;
	public static final int ANIM_WALK_LEFT = 4;
	public static final int ANIM_WALK_RIGHT = 5;
	public static final int ANIM_SIZE = 6;
	
	public float scale = 0;

	private Config config;
	private Config properties;

	private float boxX, boxY;

	private float speed;
	private float speedChange = 0.5f;
	private float minSpeed, maxSpeed;

	/**
	 * Player.java constructor.
	 * 
	 * @param transform
	 *            {@code Transform}
	 * @param hasEntityCollision
	 *            {@code Boolean}
	 */
	public EntityPlayer(Transform transform, boolean hasEntityCollision) {
		super("player", ANIM_SIZE, transform, hasEntityCollision, true);

		// Initialize player config
		String config_path = path;
		config = new Config(config_path + "player.config", Config.RES);
		String anim = (String) config.getProperty("player.anim", PropertyType.STRING);

		// Set bounding box
		float max;
		if ((boolean) config.getProperty("player.size.hasCap", PropertyType.BOOLEAN)) {
			max = 100f;
		} else {
			max = 6f;
		}
		scale = (float) config.getProperty("player.size", PropertyType.FLOAT);
		if (scale > max)
			scale = max;

		bounding_box = new AABB(new Vector2f(transform.pos.x, transform.pos.y),
				new Vector2f(transform.scale.x * scale, transform.scale.y * scale));

		// Initialize player properties
		String properties_path = config_path + anim + "/";
		properties = new Config(properties_path + "entity.properties", Config.RES);

		// Get player speed
		minSpeed = (float) properties.getProperty("player.minSpeed", PropertyType.FLOAT);
		maxSpeed = (float) properties.getProperty("player.maxSpeed", PropertyType.FLOAT);
		speed = maxSpeed / 2;

		// Set player animations
		int idle_frame_count = (int) properties.getProperty("player.animation.idle_frame_count", PropertyType.INT);
		int walking_frame_count = 0;

		setAnimation(ANIM_IDLE, idle_frame_count, 3, "player/" + anim + "/idle");
		if (properties.hasProperty("player.animation.walking_frame_count")) {
			// Set default walking animation
			walking_frame_count = (int) properties.getProperty("player.animation.walking_frame_count",
					PropertyType.INT);
			setAnimation(ANIM_WALK, walking_frame_count, 4, "player/" + anim + "/walking");
		} else {
			// Set walking down animation
			if (properties.hasProperty("player.animation.walking_down_frame_count")) {
				walking_frame_count = (int) properties.getProperty("player.animation.walking_down_frame_count",
						PropertyType.INT);
				setAnimation(ANIM_WALK_DOWN, walking_frame_count, 8, "player/" + anim + "/walking_down");
			}

			// Set walking up animation
			if (properties.hasProperty("player.animation.walking_up_frame_count")) {
				walking_frame_count = (int) properties.getProperty("player.animation.walking_up_frame_count",
						PropertyType.INT);
				setAnimation(ANIM_WALK_UP, walking_frame_count, 8, "player/" + anim + "/walking_up");
			}

			// Set walking left animation
			if (properties.hasProperty("player.animation.walking_left_frame_count")) {
				walking_frame_count = (int) properties.getProperty("player.animation.walking_left_frame_count",
						PropertyType.INT);
				setAnimation(ANIM_WALK_LEFT, walking_frame_count, 8, "player/" + anim + "/walking_left");
			}

			// Set walking right animation
			if (properties.hasProperty("player.animation.walking_right_frame_count")) {
				walking_frame_count = (int) properties.getProperty("player.animation.walking_right_frame_count",
						PropertyType.INT);
				setAnimation(ANIM_WALK_RIGHT, walking_frame_count, 8, "player/" + anim + "/walking_right");
			}
		}
	}

	@Override
	public void update(float delta, Window window, Camera camera, World world) {
		Vector2f movement = new Vector2f();

		input(delta, window);
		try {
			controllerInput(delta, window);
		} catch (IOException e) {
			e.printStackTrace();
		}

		movement.add(boxX, boxY);
		boxX = 0;
		boxY = 0;

		move(movement);
		if (movement.x != 0 || movement.y != 0) {
			// Check what walking animation to use
			if (properties.hasProperty("player.animation.walking_frame_count")) {
				useAnimation(ANIM_WALK);
			} else {
				// Use walking down animation
				if (movement.y < 0)
					if (properties.hasProperty("player.animation.walking_down_frame_count"))
						useAnimation(ANIM_WALK_DOWN);
				// Use walking up animation
				if (movement.y > 0)
					if (properties.hasProperty("player.animation.walking_up_frame_count"))
						useAnimation(ANIM_WALK_UP);
				// Use walking left animation
				if (movement.x < 0
						|| (!properties.hasProperty("player.animation.walking_down_frame_count") && movement.y < 0))
					if (properties.hasProperty("player.animation.walking_left_frame_count"))
						useAnimation(ANIM_WALK_LEFT);
				// Use walking right animation
				if (movement.x > 0
						|| (!properties.hasProperty("player.animation.walking_up_frame_count") && movement.y > 0))
					if (properties.hasProperty("player.animation.walking_right_frame_count"))
						useAnimation(ANIM_WALK_RIGHT);
			}
		} else {
			useAnimation(ANIM_IDLE);
		}

		camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()),
				(float) config.getProperty("player.smoothCamera", PropertyType.FLOAT));
	}

	/**
	 * Handles keyboard input
	 * 
	 * @param delta
	 *            {@code Float}
	 * @param window
	 *            {@code Window}
	 */
	private void input(float delta, Window window) {
		Input input = window.getInput();

		// Move player with WASD & Arrow keys
		if (input.isKeyDown(GLFW_KEY_W) || input.isKeyDown(GLFW_KEY_UP)) {
			boxY += speed * delta;
		}
		if (input.isKeyDown(GLFW_KEY_S) || input.isKeyDown(GLFW_KEY_DOWN)) {
			boxY -= speed * delta;
		}
		if (input.isKeyDown(GLFW_KEY_A) || input.isKeyDown(GLFW_KEY_LEFT)) {
			boxX -= speed * delta;
		}
		if (input.isKeyDown(GLFW_KEY_D) || input.isKeyDown(GLFW_KEY_RIGHT)) {
			boxX += speed * delta;
		}

		// Speed up & down
		if (input.isKeyDown(GLFW_KEY_KP_ADD)) {
			speed += speedChange;
			if (speed > maxSpeed)
				speed = maxSpeed;
		}
		if (input.isKeyDown(GLFW_KEY_KP_SUBTRACT)) {
			speed -= speedChange;
			if (speed < minSpeed)
				speed = minSpeed;
		}
		// System.out.println(speed);
	}

	/**
	 * Handles controller input.
	 * 
	 * @param delta
	 *            {@code Float}
	 * @param window
	 *            {@code Window}
	 * @throws IOException
	 *             {@code IOException}
	 */
	private void controllerInput(float delta, Window window) throws IOException {
		java.nio.FloatBuffer joystickAxes = org.lwjgl.BufferUtils.createFloatBuffer(GLFW_JOYSTICK_LAST);
		java.nio.ByteBuffer joystickButtons = org.lwjgl.BufferUtils.createByteBuffer(GLFW_JOYSTICK_LAST);

		if (glfwGetJoystickButtons(GLFW_JOYSTICK_1) != null && glfwGetJoystickAxes(GLFW_JOYSTICK_1) != null) {
			joystickAxes.put(glfwGetJoystickAxes(GLFW_JOYSTICK_1));
			joystickButtons.put(glfwGetJoystickButtons(GLFW_JOYSTICK_1));

			// Up & Down
			if (joystickAxes.get(GLFW_JOYSTICK_2) >= 1) {
				boxY += speed * delta;
			}
			if (joystickAxes.get(GLFW_JOYSTICK_2) <= -1) {
				boxY -= speed * delta;
			}

			// Left & Right
			if (joystickAxes.get(GLFW_JOYSTICK_3) <= -1) {
				boxX -= speed * delta;
			}
			if (joystickAxes.get(GLFW_JOYSTICK_3) >= 1) {
				boxX += speed * delta;
			}

			// Exit when 'start' is pressed
			if (joystickButtons.get(GLFW_JOYSTICK_8) == 1) {
				glfwSetWindowShouldClose(window.getWindow(), true);
			}
			// Speed up & down
			if (joystickButtons.get(GLFW_JOYSTICK_11) == 1) {
				speed += speedChange;
				if (speed > maxSpeed)
					speed = maxSpeed;
			}
			if (joystickButtons.get(GLFW_JOYSTICK_13) == 1) {
				speed -= speedChange;
				if (speed < minSpeed)
					speed = minSpeed;
			}

			joystickAxes.flip();
			joystickButtons.flip();
			// System.out.println("Speed: " + speed);
		}
	}
}
