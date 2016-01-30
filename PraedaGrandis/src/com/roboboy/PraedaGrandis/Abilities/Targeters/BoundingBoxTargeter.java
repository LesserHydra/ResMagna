package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

public class BoundingBoxTargeter extends Targeter
{
	private final double x;
	private final double y;
	private final double z;
	
	public BoundingBoxTargeter(BlockArguments args) {
		x = args.getDouble("x", 0D, true);
		y = args.getDouble("y", 0D, true);
		z = args.getDouble("z", 0D, true);
	}
	
	@Override
	public List<Target> getTargets(Target currentTarget)
	{
		LivingEntity targetEntity = currentTarget.get();
		List<Target> results = new LinkedList<>();
		
		//For all entities in bounding box
		for (Entity e : targetEntity.getNearbyEntities(x, y, z)) {
			//If living and not target entity, add
			if (!(e instanceof LivingEntity) || e.equals(targetEntity)) continue;
			results.add(currentTarget.target((LivingEntity) e));
		}
		
		return results;
	}

}
