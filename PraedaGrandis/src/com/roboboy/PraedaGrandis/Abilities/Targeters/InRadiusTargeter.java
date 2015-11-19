package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class InRadiusTargeter implements Targeter
{
	double radius;
	
	public InRadiusTargeter(double radius) {
		this.radius = radius;
	}
	
	@Override
	public MultiTarget getTargets(Target currentTarget)
	{
		List<LivingEntity> targets = new LinkedList<>();
		
		//FIXME: Not technically radius.
		for (Entity e : currentTarget.getHolder().getNearbyEntities(radius, radius, radius)) {
			if (e instanceof LivingEntity) targets.add((LivingEntity) e);
		}
		
		return new MultiTarget(targets, currentTarget);
	}

}
