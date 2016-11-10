package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import org.bukkit.entity.LivingEntity;

class IgniteAbility implements Ability.WithEntity {
	
	private final int duration;
	
	IgniteAbility(ArgumentBlock args) {
		duration = args.getInteger(false, 0,	"duration", "time", "ticks", "d", "t", null);
	}

	@Override
	public void run(LivingEntity target) {
		target.setFireTicks(duration);
	}

}
