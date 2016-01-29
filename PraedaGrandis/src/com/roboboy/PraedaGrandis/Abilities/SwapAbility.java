package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import com.roboboy.PraedaGrandis.Abilities.Targeters.DefaultTargeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

public class SwapAbility extends Ability
{
	private final Targeter otherTargeter;
	final private boolean swapFacing;
	
	public SwapAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		otherTargeter = args.getTargeter("other", new DefaultTargeter(), true);
		swapFacing = args.getBoolean("swapfacing", false, false);
	}

	@Override
	protected void execute(Target target) {
		Target otherTarget = otherTargeter.getRandomTarget(target);
		if (otherTarget == null) return;
		
		LivingEntity targetEntity = target.get();
		LivingEntity otherEntity = otherTarget.get();
		
		Location targetLoc = targetEntity.getLocation();
		Location otherLoc = otherEntity.getLocation();
		
		//Preserve old facing directions
		if (!swapFacing) {
			Vector temp = targetLoc.getDirection();
			targetLoc.setDirection(otherLoc.getDirection());
			otherLoc.setDirection(temp);
		}
		
		targetEntity.teleport(otherLoc);
		otherEntity.teleport(targetLoc);
	}

}
