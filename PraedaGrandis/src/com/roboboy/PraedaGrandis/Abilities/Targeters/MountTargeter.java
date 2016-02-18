package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;


public class MountTargeter extends Targeter
{
	@Override
	public List<Target> getTargets(Target currentTarget) {
		Entity mount = currentTarget.getEntity().getVehicle();
		if (!(mount instanceof LivingEntity)) mount = null;
		return Arrays.asList(currentTarget.target((LivingEntity)mount));
	}
}
