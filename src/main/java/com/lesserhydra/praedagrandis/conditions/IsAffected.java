package com.lesserhydra.praedagrandis.conditions;

import com.lesserhydra.praedagrandis.arguments.ArgumentBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class IsAffected implements Condition.ForEntity {
	
	private final PotionEffectType type;
	private final int amplifier;
	
	IsAffected(ArgumentBlock args) {
		//TODO: temp
		this.type = PotionEffectType.getByName(args.getString(true, "ABSORPTION", "potiontype", "potion", "type", "name", "t", null));
		this.amplifier = args.getInteger(false, 512,	"amplifier", "level", "amp", "a");
	}
	
	@Override
	public boolean test(LivingEntity target) {
		PotionEffect effect = target.getPotionEffect(type);
		//Is affected
		return effect != null
				//Match amplifier
				&& (amplifier == 512 || effect.getAmplifier() == amplifier);
	}
	
}
