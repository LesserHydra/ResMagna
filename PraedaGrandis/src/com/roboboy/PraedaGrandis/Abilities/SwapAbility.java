package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.NoneTargeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class SwapAbility extends Ability
{
	private final Targeter otherTargeter;
	private final boolean swapFacing;
	
	public SwapAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		otherTargeter = args.getTargeter(true, new NoneTargeter(),	"mount", "other", "target", "m", null);
		swapFacing = args.getBoolean(false, false,					"swapfacing", "facing", "f");
	}

	@Override
	protected void execute(Target target) {
		LivingEntity targetEntity = target.getEntity();
		if (targetEntity == null) return;
		
		Target otherTarget = otherTargeter.getRandomTarget(target);
		LivingEntity otherEntity = otherTarget.getEntity();
		if (otherEntity == null) return;
		
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
