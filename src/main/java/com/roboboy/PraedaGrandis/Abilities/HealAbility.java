package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class HealAbility extends Ability
{
	private final double healAmount;
	
	public HealAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		healAmount = args.getDouble(true, 0D,	"amount", "a", null);
	}

	@Override
	protected void execute(Target target) {
		LivingEntity targetEntity = target.getEntity();
		if (targetEntity == null) return;
		
		double newHealth = targetEntity.getHealth() + healAmount;
		if (newHealth <= targetEntity.getMaxHealth()) {
			targetEntity.setHealth(newHealth);
		}
	}

}
