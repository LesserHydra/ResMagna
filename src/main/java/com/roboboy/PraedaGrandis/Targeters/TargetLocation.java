package com.roboboy.PraedaGrandis.Targeters;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class TargetLocation implements TargetConstruct
{
	private final Location targetLocation;
	
	public TargetLocation(Location targetEntity) {
		this.targetLocation = targetEntity;
	}
	
	@Override
	public Location getLocation() {
		return targetLocation;
	}
	
	@Override
	public LivingEntity getEntity() {
		return null;
	}
	
	@Override
	public boolean isNull() {
		return (targetLocation == null);
	}
	
}
