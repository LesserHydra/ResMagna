package com.roboboy.PraedaGrandis.Abilities.Targeters;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class TargetEntity implements TargetConstruct
{
	private final LivingEntity targetEntity;
	
	public TargetEntity(LivingEntity targetEntity) {
		this.targetEntity = targetEntity;
	}
	
	@Override
	public Location getLocation() {
		if (targetEntity == null) return null;
		return targetEntity.getLocation();
	}
	
	@Override
	public LivingEntity getEntity() {
		return targetEntity;
	}
	
	@Override
	public boolean isNull() {
		return (targetEntity == null);
	}
	
}
