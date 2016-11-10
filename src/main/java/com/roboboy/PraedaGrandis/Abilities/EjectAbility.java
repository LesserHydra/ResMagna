package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Targeters.Target;
import org.bukkit.entity.LivingEntity;

class EjectAbility implements Ability {
	
	@Override
	public void execute(Target target) {
		LivingEntity targetEntity = target.asEntity();
		if (targetEntity == null) return;
		
		targetEntity.eject();
	}

}
