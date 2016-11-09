package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import org.bukkit.entity.LivingEntity;

class IgniteAbility implements Ability {
	
	private final int duration;
	
	IgniteAbility(BlockArguments args) {
		duration = args.getInteger(false, 0,	"duration", "time", "ticks", "d", "t", null);
	}

	@Override
	public void execute(Target target) {
		LivingEntity targetEntity = target.getEntity();
		if (targetEntity == null) return;
		
		targetEntity.setFireTicks(duration);
	}

}
