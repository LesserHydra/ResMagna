package com.roboboy.PraedaGrandis.Abilities.Conditions;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.BlockMask;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

class IsBlock implements Condition {
	
	private final BlockMask mask;
	private final GrandLocation location;
	
	IsBlock(BlockArguments args) {
		mask = args.getBlockMask(true, BlockMask.buildBlank(), "materials", "types", "material", "type", "mask", "mat", "m", null);
		location = args.getLocation(false, new GrandLocation(), "location", "loc", "l");
	}

	@Override
	public boolean test(Target target) { return mask.testLocation(location.calculate(target)); }

}
