package com.roboboy.bukkitutil;

import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.Location;

public class AreaEffectTools {
	
	/**
	 * Runs something at locations in a cuboid region that pass a given tester
	 * @param center Center location
	 * @param width Spread along X axis
	 * @param height Spread along Y axis
	 * @param depth Spread along Z axis
	 * @param tester Test to run at locations
	 * @param runner Runner to run at locations that pass
	 */
	public static void runInCuboid(Location center, double width, double height, double depth, Predicate<Location> tester, Consumer<Location> runner) {
		double centerX = center.getX();
		double centerY = center.getY();
		double centerZ = center.getZ();
		
		for (double x = centerX - width; x <= centerX + width; x++) {
			for (double z = centerZ - depth; z <= centerZ + depth; z++) {
				for (double y = centerY - height; y <= centerY + height; y++) {
					Location location = new Location(center.getWorld(), x, y, z);
					if (tester.test(location.clone())) runner.accept(location.clone());
				}
			}
		}
	}
	
	/**
	 * Runs something at locations in a spherical region that pass a given tester.
	 * @param center Center of the sphere
	 * @param radius Radius of the sphere
	 * @param hollow Whether the sphere should be hollow
	 * @param tester Test to run at locations
	 * @param runner Runner to run at locations that pass
	 */
	public static void runInSphere(Location center, double radius, boolean hollow, Predicate<Location> tester, Consumer<Location> runner) {
		double centerX = center.getX();
		double centerY = center.getY();
		double centerZ = center.getZ();
		
		double radSquared = radius * radius;
		double subRadSquared = (radius - 1) * (radius - 1);
		
		for (double x = centerX - radius; x <= centerX + radius; x++) {
			for (double z = centerZ - radius; z <= centerZ + radius; z++) {
				for (double y = centerY - radius; y < centerY + radius; y++) {
					double distSquared = (centerX - x) * (centerX - x) + (centerZ - z) * (centerZ - z) + (centerY - y) * (centerY - y);
					if (distSquared >= radSquared) continue;
					if (hollow && distSquared < subRadSquared) continue;
					Location location = new Location(center.getWorld(), x, y, z);
					if (tester.test(location.clone())) runner.accept(location.clone());
				}
			}
		}
	}

}
