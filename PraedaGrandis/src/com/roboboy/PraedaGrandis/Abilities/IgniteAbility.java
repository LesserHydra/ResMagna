package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class IgniteAbility extends Ability
{
	private final int duration;
	
	public IgniteAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		int duration = args.getInteger(null, 0, false);
		this.duration = args.getInteger("duration", duration, false);
	}

	@Override
	protected void execute(Target target) {
		LivingEntity targetEntity = target.getEntity();
		targetEntity.setFireTicks(duration);
	}

}
