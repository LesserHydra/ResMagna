package com.roboboy.bukkitutil;

import org.bukkit.Location;

public interface LocationTester {

	/**
	 * Runs a test at a given location. Must not have side-effects.
	 * @param location Copy of location to test
	 * @return Whether the test was passed
	 */
	public boolean testLocation(Location location);

}
