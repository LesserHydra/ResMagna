package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

public class EjectAbility extends Ability
{
	public EjectAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter) {
		super(slotType, activator, targeter);
	}

	@Override
	protected void execute(Target target) {
		target.get().eject();
	}

}
