package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.Location;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;

class IsSheltered implements Condition {
	
	@Override
	public boolean test(Target target) {
		Location loc = target.getLocation();
		return loc != null && loc.getBlockY() < loc.getWorld().getHighestBlockYAt(loc);
	}
}
