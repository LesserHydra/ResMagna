package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

class LocationTargeter extends Targeter
{
	private final GrandLocation grandLocation;
	
	public LocationTargeter(BlockArguments args) {
		grandLocation = args.getLocation(null, new GrandLocation(), true);
	}
	
	@Override
	public List<Target> getTargets(Target currentTarget) {
		Location location = grandLocation.calculate(currentTarget);
		return Arrays.asList(currentTarget.target(location));
	}

}
