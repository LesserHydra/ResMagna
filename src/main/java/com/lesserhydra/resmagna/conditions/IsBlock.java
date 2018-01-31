package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.arguments.BlockMask;
import com.lesserhydra.resmagna.arguments.GrandLocation;

class IsBlock implements Condition {
	
	private final BlockMask mask;
	private final GrandLocation location;
	
	IsBlock(ArgumentBlock args) {
		mask = args.getBlockMask(true, BlockMask.buildBlank(), "materials", "types", "material", "type", "mask", "mat", "m", null);
		location = args.getLocation(false, new GrandLocation(), "location", "loc", "l");
	}

	@Override
	public boolean test(Target target) { return mask.testLocation(location.calculate(target)); }

}
