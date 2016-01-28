package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class InBoundingBox implements Targeter
{
	double radius;
	
	//TODO: Use x, y, and z spread instead of radius
	public InBoundingBox(double radius) {
		this.radius = radius;
	}
	
	@Override
	public MultiTarget getTargets(Target currentTarget)
	{
		LivingEntity targetEntity = currentTarget.get();
		List<LivingEntity> targets = new LinkedList<>();
		
		//For all entities in bounding box
		for (Entity e : targetEntity.getNearbyEntities(radius, radius, radius)) {
			//If living and not target entity, add
			if (e instanceof LivingEntity && !e.equals(targetEntity)) targets.add((LivingEntity) e);
		}
		
		return new MultiTarget(targets, currentTarget);
	}

}
