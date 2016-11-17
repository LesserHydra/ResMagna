package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import org.bukkit.entity.LivingEntity;

class HealAbility implements Ability.ForEntity {
	
	private final double healAmount;
	
	HealAbility(ArgumentBlock args) {
		healAmount = args.getDouble(true, 0D,	"amount", "a", null);
		//TODO: amount must be non negative
	}

	@Override
	public void run(LivingEntity target) {
		double newHealth = target.getHealth() + healAmount;
		if (newHealth <= target.getMaxHealth()) target.setHealth(newHealth);
		else target.setHealth(target.getMaxHealth());
	}

}
