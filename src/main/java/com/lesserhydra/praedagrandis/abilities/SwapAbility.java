package com.lesserhydra.praedagrandis.abilities;

import com.lesserhydra.praedagrandis.arguments.ArgumentBlock;
import com.lesserhydra.praedagrandis.targeters.Target;
import com.lesserhydra.praedagrandis.targeters.Targeter;
import com.lesserhydra.praedagrandis.targeters.Targeters;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

class SwapAbility implements Ability {
	
	private final Targeter otherTargeter;
	private final boolean swapFacing;
	
	SwapAbility(ArgumentBlock args) {
		otherTargeter = args.getTargeter(true, Targeters.NONE,  "mount", "other", "target", "m", null);
		swapFacing = args.getBoolean(false, false,          	"swapfacing", "facing", "f");
	}

	@Override
	public void run(Target target) {
		LivingEntity targetEntity = target.asEntity();
		if (targetEntity == null) return;
		
		Target otherTarget = otherTargeter.getRandomTarget(target);
		LivingEntity otherEntity = otherTarget.asEntity();
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
