package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeter;
import com.lesserhydra.resmagna.targeters.Targeters;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import org.bukkit.entity.LivingEntity;

class MountAbility implements Ability {
	
	private final Targeter otherTargeter;
	
	MountAbility(ArgumentBlock args) {
		otherTargeter = args.getTargeter(true, Targeters.NONE,  "mount", "other", "target", "t", null);
	}

	@Override
	public void run(Target target) {
		LivingEntity targetEntity = target.asEntity();
		if (targetEntity == null) return;
		
		Target otherTarget = otherTargeter.getRandomTarget(target);
		LivingEntity mountEntity = otherTarget.asEntity();
		if (mountEntity == null) return;
		
		mountEntity.setPassenger(target.asEntity());
	}

}
