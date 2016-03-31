package com.roboboy.bukkitutil;

import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.Location;

public class AreaEffectTools {
	
	/**
	 * Runs a Consumer at locations within a cuboid region.
	 * @param center Center of the region
	 * @param halfWidth Half distance along X axis
	 * @param halfHeight Half distance along Y axis
	 * @param halfDepth Half distance along Z axis
	 * @param tester Predicate to test locations with
	 * @param runner Consumer for locations that pass
	 */
	public static void runInCuboid(Location center, double halfWidth, double halfHeight, double halfDepth, Predicate<Location> tester, Consumer<Location> runner) {
		double centerX = center.getX();
		double centerY = center.getY();
		double centerZ = center.getZ();
		
		for (double x = centerX - halfWidth; x <= centerX + halfWidth; x++) {
			for (double z = centerZ - halfDepth; z <= centerZ + halfDepth; z++) {
				for (double y = centerY - halfHeight; y <= centerY + halfHeight; y++) {
					Location location = new Location(center.getWorld(), x, y, z);
					if (tester.test(location.clone())) runner.accept(location.clone());
				}
			}
		}
	}
	
	/**
	 * Runs a Consumer at locations within a spherical region.
	 * @param center Center of the region
	 * @param radius Radius of the region
	 * @param hollow Whether the sphere should be hollow
	 * @param tester Predicate to test locations with
	 * @param runner Consumer for locations that pass
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
