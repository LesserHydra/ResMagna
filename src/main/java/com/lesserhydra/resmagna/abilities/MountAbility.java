package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeter;
import com.lesserhydra.resmagna.targeters.Targeters;

class MountAbility implements Ability {
	
	private final Targeter otherTargeter;
	
	MountAbility(ArgumentBlock args) {
		otherTargeter = args.getTargeter(true, Targeters.NONE,  "mount", "other", "target", "t", null);
	}

	@Override
	public void run(Target target) {
		//Require entity target
		if (!target.isEntity()) return;
		
		//Get other, and require entity
		Target otherTarget = otherTargeter.getRandomTarget(target);
		if (!otherTarget.isEntity()) return;
		
		//Mount
		otherTarget.asEntity().setPassenger(target.asEntity());
	}

}
