package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import org.bukkit.entity.LivingEntity;

class HealAbility implements Ability.Entity {
	
	private final double healAmount;
	
	HealAbility(ArgumentBlock args) {
		healAmount = args.getDouble(true, 0D,	"amount", "a", null);
	}

	@Override
	public void run(LivingEntity target) {
		double newHealth = target.getHealth() + healAmount;
		if (newHealth <= target.getMaxHealth()) {
			target.setHealth(newHealth);
		}
	}

}
