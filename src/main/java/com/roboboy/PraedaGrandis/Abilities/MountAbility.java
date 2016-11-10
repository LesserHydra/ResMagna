package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Function.Functor;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Targeters.Targeters;
import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import org.bukkit.entity.LivingEntity;

class MountAbility implements Functor {
	
	private final Targeter otherTargeter;
	
	MountAbility(ArgumentBlock args) {
		otherTargeter = args.getTargeter(true, Targeters.NONE,  "mount", "other", "target", "t", null);
	}

	@Override
	public void run(Target target) {
		LivingEntity targetEntity = target.asEntity();
		if (targetEntity == null) return;
		
		Target otherTarget = otherTargeter.getRandomTarget(target);
		LivingEntity mountEntity = otherTarget.asEntity();
		if (mountEntity == null) return;
		
		mountEntity.setPassenger(target.asEntity());
	}

}
