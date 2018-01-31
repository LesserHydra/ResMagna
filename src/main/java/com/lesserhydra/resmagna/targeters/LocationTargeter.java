package com.lesserhydra.resmagna.targeters;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import org.bukkit.Location;

class LocationTargeter implements Targeter.Singleton {
	
	private final GrandLocation grandLocation;
	
	LocationTargeter(ArgumentBlock args) {
		grandLocation = args.getLocation(true, new GrandLocation(), "location", "loc", "l", null);
	}
	
	@Override
	public Target getTarget(Target currentTarget) {
		Location location = grandLocation.calculate(currentTarget);
		return currentTarget.target(Target.from(location));
	}

}
