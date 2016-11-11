package com.lesserhydra.praedagrandis.conditions;

import com.lesserhydra.praedagrandis.arguments.ArgumentBlock;
import com.lesserhydra.praedagrandis.targeters.Target;
import com.lesserhydra.praedagrandis.arguments.BlockMask;
import com.lesserhydra.praedagrandis.arguments.GrandLocation;

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
