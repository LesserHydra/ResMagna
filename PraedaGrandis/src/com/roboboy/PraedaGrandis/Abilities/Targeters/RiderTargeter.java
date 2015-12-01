package com.roboboy.PraedaGrandis.Abilities.Targeters;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;


public class RiderTargeter implements Targeter
{
	@Override
	public MultiTarget getTargets(Target currentTarget) {
		Entity rider = currentTarget.get().getPassenger();
		if (!(rider instanceof LivingEntity)) rider = null;
		return new MultiTarget((LivingEntity)rider, currentTarget);
	}
}
