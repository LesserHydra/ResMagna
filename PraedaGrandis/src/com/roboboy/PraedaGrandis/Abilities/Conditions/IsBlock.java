package com.roboboy.PraedaGrandis.Abilities.Conditions;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.BlockMask;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

class IsBlock extends Condition
{
	private final BlockMask mask;
	private final GrandLocation location;
	
	public IsBlock(Targeter targeter, boolean not, BlockArguments args) {
		super(targeter, not);
		mask = args.getBlockMask(true, BlockMask.buildBlank(),		"materials", "types", "material", "type", "mask", "mat", "m", null);
		location = args.getLocation(false, new GrandLocation(),		"location", "loc", "l");
	}

	@Override
	public boolean checkThis(Target target) {
		return mask.matches(location.calculate(target));
	}

}
