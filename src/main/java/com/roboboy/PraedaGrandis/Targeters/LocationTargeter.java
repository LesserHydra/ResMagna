package com.roboboy.PraedaGrandis.Targeters;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import com.roboboy.PraedaGrandis.Arguments.GrandLocation;
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
