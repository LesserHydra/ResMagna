package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeters;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import org.bukkit.entity.LivingEntity;

class MountAbility implements Ability {
	
	private final Targeter otherTargeter;
	
	MountAbility(BlockArguments args) {
		otherTargeter = args.getTargeter(true, Targeters.NONE,  "mount", "other", "target", "t", null);
	}

	@Override
	public void execute(Target target) {
		LivingEntity targetEntity = target.getEntity();
		if (targetEntity == null) return;
		
		Target otherTarget = otherTargeter.getRandomTarget(target);
		LivingEntity mountEntity = otherTarget.getEntity();
		if (mountEntity == null) return;
		
		mountEntity.setPassenger(target.getEntity());
	}

}
