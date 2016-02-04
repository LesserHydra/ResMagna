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
		duration = args.getInteger("duration", 0, true);
	}

	@Override
	protected void execute(Target target) {
		LivingEntity targetEntity = target.get();
		targetEntity.setFireTicks(duration);
	}

}
