package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.FunctionRunner;

class CustomAbility extends Ability
{
	private FunctionRunner grandAbility;
	
	public CustomAbility(String name, ItemSlotType slotType, ActivatorType activator, Targeter targeter)
	{
		super(slotType, activator, targeter);
		grandAbility = new FunctionRunner(name);
	}
	
	@Override
	protected void execute(Target target) {
		grandAbility.run(target);
	}

}
