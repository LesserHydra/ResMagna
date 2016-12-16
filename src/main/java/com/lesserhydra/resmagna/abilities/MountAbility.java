package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeters;

class MountAbility implements Ability {
	
	private final Evaluators.ForEntity otherTargeter;
	
	MountAbility(ArgumentBlock args) {
		otherTargeter = args.getEntity(true, Targeters.NONE,  "mount", "other", "target", "t", null);
	}

	@Override
	public void run(Target target) {
		//Require entity target
		if (!target.isEntity()) {
			GrandLogger.log("Tried to run mount ability with invalid target.", LogType.RUNTIME_ERRORS);
			return;
		}
		
		//Verify parameter
		if (!otherTargeter.evaluate(target)) return;
		
		//Mount
		otherTargeter.get().setPassenger(target.asEntity());
	}

}
