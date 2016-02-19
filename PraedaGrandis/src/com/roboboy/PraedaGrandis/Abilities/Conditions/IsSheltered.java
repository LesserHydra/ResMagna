package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.Location;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

class IsSheltered extends Condition
{
	public IsSheltered(Targeter targeter, boolean not) {
		super(targeter, not);
	}

	@Override
	protected boolean checkThis(Target target) {
		Location targetLocation = target.getLocation();
		return targetLocation.getBlockY() < targetLocation.getWorld().getHighestBlockYAt(targetLocation);
	}
}
