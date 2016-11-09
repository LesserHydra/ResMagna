package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class HealAbility implements Ability {
	
	private final double healAmount;
	
	HealAbility(BlockArguments args) {
		healAmount = args.getDouble(true, 0D,	"amount", "a", null);
	}

	@Override
	public void execute(Target target) {
		LivingEntity targetEntity = target.getEntity();
		if (targetEntity == null) return;
		
		double newHealth = targetEntity.getHealth() + healAmount;
		if (newHealth <= targetEntity.getMaxHealth()) {
			targetEntity.setHealth(newHealth);
		}
	}

}
