package com.lesserhydra.resmagna.targeters;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;

class BoundingBoxTargeter implements Targeter {
	
	private final double spreadX;
	private final double spreadY;
	private final double spreadZ;
	
	BoundingBoxTargeter(ArgumentBlock args) {
		double spread = args.getDouble(false, 0.5,		"spread", "radius", "sprd", "r");
		double spreadH = args.getDouble(false, spread,	"spreadh", "sprdh", "sh", "rh");
		double spreadV = args.getDouble(false, spread,	"spreadv", "sprdv", "sv", "rv");
		spreadX = args.getDouble(false, spreadH,		"spreadx", "sx", "x");
		spreadY = args.getDouble(false, spreadV,		"spready", "sy", "y");
		spreadZ = args.getDouble(false, spreadH,		"spreadz", "sz", "z");
	}
	
	@Override
	public List<Target> getTargets(Target currentTarget) {
		if (currentTarget.isNull()) return Collections.emptyList();
		LivingEntity targetEntity = currentTarget.asEntity();
		Location targetLocation = currentTarget.asLocation();
		
		//All LivingEntities in bounding box other than currentTarget, if exists
		return targetLocation.getWorld().getNearbyEntities(targetLocation, spreadX, spreadY, spreadZ).stream()
				.filter(e -> e instanceof LivingEntity)
				.filter(e -> !e.equals(targetEntity))
				.map(e -> currentTarget.target(Target.from((LivingEntity) e)))
				.collect(Collectors.toList());
	}

}
