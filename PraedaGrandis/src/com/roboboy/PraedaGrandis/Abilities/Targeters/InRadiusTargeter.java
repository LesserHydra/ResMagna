package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class InRadiusTargeter implements Targeter
{
	double radius;
	double radiusSquared;
	
	public InRadiusTargeter(double radius) {
		this.radius = radius;
		this.radiusSquared = Math.pow(radius, 2);
	}
	
	@Override
	public MultiTarget getTargets(Target currentTarget)
	{
		LivingEntity holder = currentTarget.getHolder();
		List<LivingEntity> targets = new LinkedList<>();
		
		//For all entities in bounding box
		for (Entity e : holder.getNearbyEntities(radius, radius, radius)) {
			//If living and not holder
			if (e instanceof LivingEntity && !e.equals(holder)) {
				//If in radius
				if (e.getLocation().distanceSquared(holder.getLocation()) <= radiusSquared) targets.add((LivingEntity) e);
			}
		}
		
		return new MultiTarget(targets, currentTarget);
	}

}
