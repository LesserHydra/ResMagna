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
		healAmount = args.getDouble("amount", 0D, true);
	}

	@Override
	protected void execute(Target target)
	{
		LivingEntity e = target.get();
		double newHealth = e.getHealth() + healAmount;
		
		if (newHealth <= e.getMaxHealth()) {
			e.setHealth(newHealth);
		}
	}

}
