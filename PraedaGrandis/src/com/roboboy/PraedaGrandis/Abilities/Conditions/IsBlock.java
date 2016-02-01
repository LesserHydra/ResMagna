package com.roboboy.PraedaGrandis.Abilities.Conditions;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;
import com.roboboy.PraedaGrandis.Configuration.GrandMaterial;

public class IsBlock extends Condition
{
	private final GrandMaterial materials;
	private final GrandLocation location;
	
	public IsBlock(Targeter targeter, boolean not, BlockArguments args)
	{
		super(targeter, not);
		materials = new GrandMaterial(args.get("types", "", true));
		location = args.getLocation("location", new GrandLocation(), true);
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
