package com.lesserhydra.resmagna.targeters;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BoundingBoxTargeter implements Targeter {
	
	private final Evaluators.ForDouble spreadX;
	private final Evaluators.ForDouble spreadY;
	private final Evaluators.ForDouble spreadZ;
	
	BoundingBoxTargeter(ArgumentBlock args) {
		Evaluators.ForDouble spread = args.getDouble(false, 0.5,		"spread", "radius", "sprd", "r");
		Evaluators.ForDouble spreadH = args.getDouble(false, spread,	"spreadh", "sprdh", "sh", "rh");
		Evaluators.ForDouble spreadV = args.getDouble(false, spread,	"spreadv", "sprdv", "sv", "rv");
		spreadX = args.getDouble(false, spreadH,		"spreadx", "sx", "x");
		spreadY = args.getDouble(false, spreadV,		"spready", "sy", "y");
		spreadZ = args.getDouble(false, spreadH,		"spreadz", "sz", "z");
	}
	
	@Override
	public List<Target> getTargets(Target currentTarget) {
		if (!currentTarget.isLocation()) return Collections.emptyList();
		Location targetLocation = currentTarget.asLocation();
		
		if (!(spreadX.evaluate(currentTarget)
				&& spreadY.evaluate(currentTarget)
				&& spreadZ.evaluate(currentTarget))) return Collections.emptyList();
		
		//Begin constructing stream over all LivingEntities in bounding box
		Stream<Entity> working = targetLocation.getWorld()
				.getNearbyEntities(targetLocation, spreadX.get(), spreadY.get(), spreadZ.get())
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
