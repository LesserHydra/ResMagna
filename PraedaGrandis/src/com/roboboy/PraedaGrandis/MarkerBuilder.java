package com.roboboy.PraedaGrandis;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;

public class MarkerBuilder
{
	public static LivingEntity buildMarker(Location location) {
		ArmorStand marker = location.getWorld().spawn(new Location(location.getWorld(), 0D, 0D, 0D), ArmorStand.class);
		marker.setVisible(false);
		marker.setMarker(true);
		marker.teleport(location);
		marker.remove();
		return marker;
	}
}
