package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;


public class RiderTargeter extends Targeter
{
	@Override
	public List<Target> getTargets(Target currentTarget) {
		Entity rider = currentTarget.get().getPassenger();
		if (!(rider instanceof LivingEntity)) rider = null;
		return Arrays.asList(currentTarget.target((LivingEntity)rider));
	}
}
