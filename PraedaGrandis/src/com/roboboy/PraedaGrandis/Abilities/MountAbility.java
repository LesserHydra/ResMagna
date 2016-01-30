package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Abilities.Targeters.DefaultTargeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

public class MountAbility extends Ability
{
	private final Targeter otherTargeter;
	
	public MountAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		otherTargeter = args.getTargeter("other", new DefaultTargeter(), true);
	}

	@Override
	protected void execute(Target target) {
		Target otherTarget = otherTargeter.getRandomTarget(target);
		if (otherTarget == null) return;
		
		otherTarget.get().setPassenger(target.get());
	}

}
