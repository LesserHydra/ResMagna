package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

class EjectAbility extends Ability
{
	public EjectAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter) {
		super(slotType, activator, targeter);
	}

	@Override
	protected void execute(Target target) {
		target.getEntity().eject();
	}

}
