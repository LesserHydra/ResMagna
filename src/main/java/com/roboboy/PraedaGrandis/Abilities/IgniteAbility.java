package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import com.roboboy.PraedaGrandis.Targeters.Target;
import org.bukkit.entity.LivingEntity;

class IgniteAbility implements Ability {
	
	private final int duration;
	
	IgniteAbility(ArgumentBlock args) {
		duration = args.getInteger(false, 0,	"duration", "time", "ticks", "d", "t", null);
	}

	@Override
	public void execute(Target target) {
		LivingEntity targetEntity = target.asEntity();
		if (targetEntity == null) return;
		
		targetEntity.setFireTicks(duration);
	}

}
