package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;

public class SwapHolderAbility extends Ability
{
	final private boolean swapFacing;
	
	public SwapHolderAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, ConfigString args) {
		super(slotType, activator, targeter);
		swapFacing = Boolean.parseBoolean(args.get(1));
	}

	@Override
	protected void execute(Target target) {
		Location targetLoc = target.get().getLocation();
		Location holderLoc = target.getHolder().getLocation();
		if (swapFacing) {
			Vector temp = targetLoc.getDirection();
			targetLoc.setDirection(holderLoc.getDirection());
			holderLoc.setDirection(temp);
		}
		
		target.get().teleport(holderLoc);
		target.getHolder().teleport(targetLoc);
	}

}
