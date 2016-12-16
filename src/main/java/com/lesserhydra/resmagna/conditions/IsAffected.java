package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class IsAffected implements Condition {
	
	private final PotionEffectType type;
	private final Evaluators.ForInt amplifier;
	
	IsAffected(ArgumentBlock args) {
		this.type = args.getPotionEffectType(true, PotionEffectType.ABSORPTION, "PotionType", "Potion", "Type", "Name", "T", null);
		this.amplifier = args.getInteger(false, 512,	"amplifier", "level", "amp", "a");
	}
	
	@Override
	public boolean test(Target target) {
		if (!target.isEntity()) return false;
		
		PotionEffect effect = target.asEntity().getPotionEffect(type);
		//Is affected
		return effect != null
				//Match amplifier
				&& (!amplifier.evaluate(target, false) || effect.getAmplifier() == amplifier.get());
	}
	
}
