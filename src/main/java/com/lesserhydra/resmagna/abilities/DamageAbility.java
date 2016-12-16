package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeters;

class DamageAbility implements Ability {
	
	private final Evaluators.ForDouble damageAmount;
	private final Evaluators.ForEntity damager;
	
	DamageAbility(ArgumentBlock args) {
		damageAmount = args.getDouble(true, 0D,     "amount", "a", null);
		damager = args.getEntity(false, Targeters.HOLDER,   "damager", "dmgr", "source");
		args.getString(false, (String)null, "cause"); //TODO
	}
	
	@Override
	public void run(Target target) {
		//Validate target
		if (!target.isEntity()) {
			GrandLogger.log("Tried to run damage ability with invalid target.", LogType.RUNTIME_ERRORS);
			return;
		}
		
		//Validate damage amount
		if (!damageAmount.evaluate(target)) return;
		
		//TODO: Need to take into account armor (already handled?), and allow to change damage cause (nms)
		//Damage target
		if (damager.evaluate(target, false)) target.asEntity().damage(damageAmount.get(), damager.get());
		else target.asEntity().damage(damageAmount.get());
	}
	
}
