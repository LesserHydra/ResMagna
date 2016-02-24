package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.entity.LivingEntity;
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
		LivingEntity targetEntity = target.getEntity();
		if (targetEntity == null) return;
		
		targetEntity.eject();
	}

}
