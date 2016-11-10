package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;

class HealAbility implements Ability {
	
	private final double healAmount;
	
	HealAbility(ArgumentBlock args) {
		healAmount = args.getDouble(true, 0D,	"amount", "a", null);
	}

	@Override
	public void execute(Target target) {
		LivingEntity targetEntity = target.asEntity();
		if (targetEntity == null) return;
		
		double newHealth = targetEntity.getHealth() + healAmount;
		if (newHealth <= targetEntity.getMaxHealth()) {
			targetEntity.setHealth(newHealth);
		}
	}

}
