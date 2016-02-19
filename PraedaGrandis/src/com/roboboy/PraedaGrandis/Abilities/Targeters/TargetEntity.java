package com.roboboy.PraedaGrandis.Abilities.Targeters;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;

class TargetEntity
{
	private Location targetLocation;
	private LivingEntity targetEntity;
	
	public TargetEntity(Location targetLocation) {
		this.targetLocation = targetLocation;
	}
	
	public TargetEntity(LivingEntity targetEntity) {
		this.targetEntity = targetEntity;
	}
	
	public Location getLocation() {
		if (targetLocation == null && targetEntity != null) return targetEntity.getLocation();
		return targetLocation;
	}
	
	public LivingEntity getEntity() {
		if (targetEntity == null && targetLocation != null) targetEntity = buildInstantMarker(targetLocation);
		return targetEntity;
	}

	public boolean isNull() {
		return (targetLocation == null && targetEntity == null);
	}
	
	/**
	 * Create an invalid marker LivingEntity in the world at the given location.<br>
	 * The returned entity will not respond to teleportations, and so cannot be moved.
	 * @param location Location to create marker at
	 * @return The marker LivingEntity
	 */
	private static LivingEntity buildInstantMarker(Location location) {
		ArmorStand marker = location.getWorld().spawn(new Location(location.getWorld(), 0D, 0D, 0D), ArmorStand.class);
		marker.setVisible(false);
		marker.setMarker(true);
		marker.setGravity(false);
		marker.teleport(location);
		marker.remove();
		return marker;
	}
	
}
