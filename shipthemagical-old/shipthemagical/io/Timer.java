package io.github.coffeecatrailway.shipthemagical.io;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class Timer {

	/**
	 * Gets time for timer.
	 * 
	 * @return {@code Double}
	 */
	public static double getTime() {
		return (double) System.nanoTime() / (double) 1000000000L;
	}
}
