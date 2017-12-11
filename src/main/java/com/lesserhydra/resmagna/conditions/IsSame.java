package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeter;
import com.lesserhydra.resmagna.targeters.Targeters;

class IsSame implements Condition {
	
	private final Targeter testTargeter;
	
	IsSame(ArgumentBlock args) {
		testTargeter = args.getTargeter(true, Targeters.NONE,    "other", null);
	}
	
	@Override
	public boolean test(Target target) {
		Target other = testTargeter.getRandomTarget(target);
		if (target.isNull()) return false;
		if (target.isEntity()) return other.isEntity() && target.asEntity().equals(other.asEntity());
		else if (target.isLocation()) return other.isLocation() && target.asLocation().equals(other.asLocation());
		throw new IllegalStateException("Target is not none, entity, or location?");
	}
	
}
