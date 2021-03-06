package com.lesserhydra.resmagna.targeters;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		if (!currentTarget.isLocation()) return Collections.emptyList();
		Location targetLocation = currentTarget.asLocation();
		
		//Begin constructing stream over all LivingEntities in bounding box
		Stream<Entity> working = targetLocation.getWorld().getNearbyEntities(targetLocation, spreadX, spreadY, spreadZ)
				.stream()
				.filter(e -> e instanceof LivingEntity);
		
		//Add exception for current entity target, if exists
		if (currentTarget.isEntity()) {
			LivingEntity targetEntity = currentTarget.asEntity();
			working = working.filter(e -> !e.equals(targetEntity));
		}
		
		//Finish steam, collecting found targets to list
		return working
				.map(e -> currentTarget.target(Target.from((LivingEntity) e)))
				.collect(Collectors.toList());
	}

}
