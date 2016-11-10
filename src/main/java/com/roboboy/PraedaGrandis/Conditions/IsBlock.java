package com.roboboy.PraedaGrandis.Conditions;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Arguments.BlockMask;
import com.roboboy.PraedaGrandis.Arguments.GrandLocation;

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
