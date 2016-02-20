package com.roboboy.PraedaGrandis.Abilities.Conditions;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;
import com.roboboy.PraedaGrandis.Configuration.GrandMaterial;

class IsBlock extends Condition
{
	private final GrandMaterial materials;
	private final GrandLocation location;
	
	public IsBlock(Targeter targeter, boolean not, BlockArguments args)
	{
		super(targeter, not);
		materials = new GrandMaterial(args.getString("", true,		"materials", "types", "material", "type", "mat", "m", "t"));
		location = args.getLocation(new GrandLocation(), false,		"location", "loc", "l");
	}

	@Override
	public boolean checkThis(Target target)
	{
		//Material blockType = location.calculate(target.getLocation()).getBlock().getType();
		if (materials.contains(location.calculate(target).getBlock().getType())) {
			return true;
		}
		else return false;
	}

}
