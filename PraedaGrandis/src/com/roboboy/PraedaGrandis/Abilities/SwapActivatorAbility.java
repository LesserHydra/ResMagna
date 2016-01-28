package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

public class SwapActivatorAbility extends Ability
{
	final private boolean swapFacing;
	
	public SwapActivatorAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		swapFacing = args.getBoolean("swapfacing", false, false);
	}

	@Override
	protected void execute(Target target) {
		Location targetLoc = target.get().getLocation();
		Location activatorLoc = target.getActivator().getLocation();
		
		//Preserve old facing directions
		if (!swapFacing) {
			Vector temp = targetLoc.getDirection();
			targetLoc.setDirection(activatorLoc.getDirection());
			activatorLoc.setDirection(temp);
		}
		
		target.get().teleport(activatorLoc);
		target.getActivator().teleport(targetLoc);
	}

}
