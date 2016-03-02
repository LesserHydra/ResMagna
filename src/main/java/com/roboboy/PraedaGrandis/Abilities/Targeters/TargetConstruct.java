package com.roboboy.PraedaGrandis.Abilities.Targeters;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public interface TargetConstruct
{
	public Location getLocation();
	
	public LivingEntity getEntity();

	public boolean isNull();
}
