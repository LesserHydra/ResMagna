package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

public class HolderMountAbility extends Ability
{
	public HolderMountAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter) {
		super(slotType, activator, targeter);
	}

	@Override
	protected void execute(Target target) {
		target.get().setPassenger(target.getHolder());
	}

}
