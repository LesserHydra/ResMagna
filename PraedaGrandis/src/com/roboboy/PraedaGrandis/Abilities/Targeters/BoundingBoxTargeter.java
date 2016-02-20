package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

public class BoundingBoxTargeter extends Targeter
{
	private final double spreadX;
	private final double spreadY;
	private final double spreadZ;
	
	public BoundingBoxTargeter(BlockArguments args) {
		double spread = args.getDouble(0.5, false,		"spread", "radius", "sprd", "r");
		double spreadH = args.getDouble(spread, false,	"spreadh", "sprdh", "sh", "rh");
		double spreadV = args.getDouble(spread, false,	"spreadv", "sprdv", "sv", "rv");
		spreadX = args.getDouble(spreadH, false,		"spreadx", "sx", "x");
		spreadY = args.getDouble(spreadV, false,		"spready", "sy", "y");
		spreadZ = args.getDouble(spreadH, false,		"spreadz", "sz", "z");
	}
	
	@Override
	public List<Target> getTargets(Target currentTarget)
	{
		LivingEntity targetEntity = currentTarget.getEntity();
		List<Target> results = new LinkedList<>();
		
		//For all entities in bounding box
		for (Entity e : targetEntity.getNearbyEntities(spreadX, spreadY, spreadZ)) {
			//If living and not target entity, add
			if (!(e instanceof LivingEntity) || e.equals(targetEntity)) continue;
			results.add(currentTarget.target((LivingEntity) e));
		}
		
		return results;
	}

}
