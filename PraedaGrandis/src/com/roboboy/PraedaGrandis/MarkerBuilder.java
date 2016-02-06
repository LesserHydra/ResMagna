package com.roboboy.PraedaGrandis;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;

public class MarkerBuilder
{
	/**
	 * Create an invalid marker LivingEntity in the world at the given location.<br>
	 * The returned entity will not respond to teleportations, and so cannot be moved.
	 * @param location Location to create marker at
	 * @return The marker LivingEntity
	 */
	public static LivingEntity buildInstantMarker(Location location) {
		LivingEntity marker = buildPersistantMarker(location);
		marker.remove();
		return marker;
	}
	
	/**
	 * Create a valid marker LivingEntity in the world at the given location. The returned entity will persist<br>
	 * in the world until removed. Ideally, this should be done right away, before the current tick ends.
	 * @param location Location to create marker at
	 * @return The marker LivingEntity
	 */
	public static LivingEntity buildPersistantMarker(Location location) {
		ArmorStand marker = location.getWorld().spawn(new Location(location.getWorld(), 0D, 0D, 0D), ArmorStand.class);
		marker.setVisible(false);
		marker.setMarker(true);
		marker.setGravity(false);
		marker.teleport(location);
		return marker;
	}
}
