package com.roboboy.PraedaGrandis.Abilities.Conditions;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;
import com.roboboy.PraedaGrandis.Configuration.GrandMaterial;

public class IsBlock extends Condition
{
	private final GrandMaterial materials;
	private final GrandLocation location;
	
	public IsBlock(Targeter targeter, boolean not, ConfigString args)
	{
		super(targeter, not);
		//TODO: Error handling. Must be three arguments (condition name + 2 args)
		materials = new GrandMaterial(args.get(1));
		location = new GrandLocation(args.get(2));
	}

	@Override
	public boolean checkThis(Target target)
	{
		//Material blockType = location.calculate(target.getLocation()).getBlock().getType();
		if (materials.contains(location.calculate(target.get().getLocation()).getBlock().getType())) {
			return true;
		}
		else return false;
	}

}
