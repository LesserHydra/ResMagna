package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeters;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

class SwapAbility implements Ability {
	
	private final Evaluators.ForEntity otherTargeter;
	private final Evaluators.ForBoolean swapFacing;
	
	SwapAbility(ArgumentBlock args) {
		this.otherTargeter = args.getEntity(true, Targeters.NONE,  "mount", "other", "target", "m", null);
		this.swapFacing = args.getBoolean(false, false,          	"swapfacing", "facing", "f");
	}

	@Override
	public void run(Target target) {
		if (!target.isEntity()) return;
		LivingEntity targetEntity = target.asEntity();
		
		if (!(otherTargeter.evaluate(target)
				&& swapFacing.evaluate(target))) return;
		
		Entity otherEntity = otherTargeter.get();
		
		Location targetLoc = targetEntity.getLocation();
		Location otherLoc = otherEntity.getLocation();
		
		//Preserve old facing directions
		if (!swapFacing.get()) {
			Vector temp = targetLoc.getDirection();
			targetLoc.setDirection(otherLoc.getDirection());
			otherLoc.setDirection(temp);
		}
		
		targetEntity.teleport(otherLoc);
		otherEntity.teleport(targetLoc);
	}

}
