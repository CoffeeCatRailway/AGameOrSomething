package io.github.coffeecatrailway.shipthemagical.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.Properties;

//import org.fusesource.jansi.AnsiConsole;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class Config {

	/**
	 * All properties the config can hold.
	 */
	public static enum PropertyType {
		STRING, INT, FLOAT, BOOLEAN, DOUBLE
	}

	public static String RES = "./res/";
	public static String LEVELS = "./levels/";

	private String path;
	private FileReader file;
	private Properties config;

	/**
	 * Config.java constructor.
	 * 
	 * @param path
	 *            {@code String}
	 * @param location
	 *            {@code String}
	 */
	public Config(String path, String location) {
		try {
			this.path = location + path;
			config = new Properties();
			file = new FileReader(this.path);

			if (file != null) {
				config.load(file);
			} else {
				throw new FileNotFoundException("Config file '" + this.path + "' could not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

//		org.fusesource.jansi.Ansi.Color c = Color.MAGENTA;
//		if (location == RES)
//			c = Color.CYAN;
//		AnsiConsole.out
//				.println("Config: [" + Color.toColor(c) + this.path + Color.toColor(Color.DEFAULT) + "] loaded at: ["
//						+ Color.toColor(c) + new File(this.path).getPath() + Color.toColor(Color.DEFAULT) + "]!");
	}

	/**
	 * Check if config has property.
	 * 
	 * @param key
	 *            {@code String}
	 * @return {@code Boolean}
	 */
	public boolean hasProperty(String key) {
		return config.containsKey(key);
	}

	/**
	 * Gets property key from config.
	 * 
	 * @param key
	 *            {@code String}
	 * @param type
	 *            {@code PropertyType}
	 * @return {@code Object}
	 */
	public Object getProperty(String key, PropertyType type) {
		Object value = null;
		switch (type) {
		case STRING:
			value = config.getProperty(key);
			return (String) value;
		case INT:
			value = Integer.valueOf(config.getProperty(key));
			return (int) value;
		case FLOAT:
			value = Float.valueOf(config.getProperty(key));
			return (float) value;
		case BOOLEAN:
			value = Boolean.valueOf(config.getProperty(key));
			return (boolean) value;
		case DOUBLE:
			value = Double.valueOf(config.getProperty(key));
			return (double) value;
		}
		return value;
	}

	/**
	 * Set property key in config.
	 * 
	 * @param key
	 *            {@code String}
	 * @param value
	 *            {@code String}
	 */
	public void setProperty(String key, String value) {
		try {
			OutputStream output = new FileOutputStream(path);
			config.setProperty(key, value);
			config.store(output, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
