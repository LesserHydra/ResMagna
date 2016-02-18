package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.MarkerBuilder;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

class LocationTargeter extends Targeter
{
	private final GrandLocation grandLocation;
	
	public LocationTargeter(BlockArguments args) {
		grandLocation = args.getLocation("location", new GrandLocation(), true);
	}
	
	@Override
	public List<Target> getTargets(Target currentTarget) {
		LivingEntity marker = MarkerBuilder.buildInstantMarker(grandLocation.calculate(currentTarget));
		return Arrays.asList(currentTarget.target(marker));
	}

}
