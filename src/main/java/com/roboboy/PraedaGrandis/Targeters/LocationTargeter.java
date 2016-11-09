package com.roboboy.PraedaGrandis.Targeters;

import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

class LocationTargeter implements Targeter {
	
	private final GrandLocation grandLocation;
	
	LocationTargeter(BlockArguments args) {
		grandLocation = args.getLocation(true, new GrandLocation(), "location", "loc", "l", null);
	}
	
	@Override
	public List<Target> getTargets(Target currentTarget) {
		Location location = grandLocation.calculate(currentTarget);
		return Collections.singletonList(currentTarget.target(location));
	}

}
