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
		double spread = args.getDouble(false, 0.5,		"spread", "radius", "sprd", "r");
		double spreadH = args.getDouble(false, spread,	"spreadh", "sprdh", "sh", "rh");
		double spreadV = args.getDouble(false, spread,	"spreadv", "sprdv", "sv", "rv");
		spreadX = args.getDouble(false, spreadH,		"spreadx", "sx", "x");
		spreadY = args.getDouble(false, spreadV,		"spready", "sy", "y");
		spreadZ = args.getDouble(false, spreadH,		"spreadz", "sz", "z");
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
