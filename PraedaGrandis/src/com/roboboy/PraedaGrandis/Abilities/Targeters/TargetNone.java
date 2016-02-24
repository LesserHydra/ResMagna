package com.roboboy.PraedaGrandis.Abilities.Targeters;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class TargetNone implements TargetConstruct
{
	@Override
	public Location getLocation() {
		return null;
	}
	
	@Override
	public LivingEntity getEntity() {
		return null;
	}
	
	@Override
	public boolean isNull() {
		return true;
	}
}
