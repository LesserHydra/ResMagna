package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class SaveTargetAbility extends Ability
{
	private final String saveName;

	public SaveTargetAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		saveName = args.get("savename", "", true);
	}

	@Override
	protected void execute(Target target) {
		target.save(saveName);
	}

}
