package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.BlockMask;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.targeters.Target;

class IsBlock implements Condition {
	
	private final Evaluators.ForBlockMask mask;
	private final Evaluators.ForLocation location;
	
	IsBlock(ArgumentBlock args) {
		mask = args.getBlockMask(true, BlockMask.buildBlank(), "materials", "types", "material", "type", "mask", "mat", "m", null);
		location = args.getLocation(false, GrandLocation.CURRENT, "location", "loc", "l");
	}

	@Override
	public boolean test(Target target) {
		if (!(mask.evaluate(target)
				&& location.evaluate(target))) return false;
		return mask.get().testLocation(location.get());
	}

}
