package com.lesserhydra.resmagna.util;

import java.util.stream.Stream;
import org.bukkit.Location;

public class AreaEffectTools {
	
	/**
	 * Gets a stream of locations in a cuboid region
	 * @param center Center of the region
	 * @param halfWidth Half distance along X axis
	 * @param halfHeight Half distance along Y axis
	 * @param halfDepth Half distance along Z axis
	 * @return Stream of locations
	 */
	public static Stream<Location> cuboidStream(Location center, double halfWidth, double halfHeight, double halfDepth) {
		double centerX = center.getX();
		double centerY = center.getY();
		double centerZ = center.getZ();
		
		Stream.Builder<Location> builder = Stream.builder();
		for (double x = centerX - halfWidth; x <= centerX + halfWidth; x++) {
			for (double z = centerZ - halfDepth; z <= centerZ + halfDepth; z++) {
				for (double y = centerY - halfHeight; y <= centerY + halfHeight; y++) {
					Location location = new Location(center.getWorld(), x, y, z);
					builder.add(location);
				}
			}
		}
		
		return builder.build();
	}
	
	/**
	 * Gets a stream of locations in a spherical region
	 * @param center Center of the region
	 * @param radius Radius of the region
	 * @param hollow Whether the sphere should be hollow
	 * @return Stream of locations
	 */
	public static Stream<Location> sphereStream(Location center, double radius, boolean hollow) {
		double radSquared = radius * radius;
		double subRadSquared = (radius - 1) * (radius - 1);
		
		return cuboidStream(center, radius, radius, radius)
				.filter(location -> {
					double distSquared = center.distanceSquared(location);
					return (distSquared <= radSquared) && (!hollow || distSquared >= subRadSquared);
				});
	}

}
