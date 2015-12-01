package com.roboboy.PraedaGrandis.Abilities.Targeters;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;


public class MountTargeter implements Targeter
{
	@Override
	public MultiTarget getTargets(Target currentTarget) {
		Entity mount = currentTarget.get().getVehicle();
		if (!(mount instanceof LivingEntity)) mount = null;
		return new MultiTarget((LivingEntity)mount, currentTarget);
	}
}
