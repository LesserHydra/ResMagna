package com.roboboy.PraedaGrandis.Abilities.Targeters;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.MarkerBuilder;

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
		if (targetEntity == null && targetLocation != null) targetEntity = MarkerBuilder.buildInstantMarker(targetLocation);
		return targetEntity;
	}

	public boolean isNull() {
		return (targetLocation == null && targetEntity == null);
	}
	
}
