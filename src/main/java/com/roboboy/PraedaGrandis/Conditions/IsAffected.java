package com.roboboy.PraedaGrandis.Conditions;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

class IsAffected implements Condition.ForEntity {
	
	private final PotionEffectType type;
	
	IsAffected(ArgumentBlock args) {
		//TODO: temp
		type = PotionEffectType.getByName(args.getString(true, "ABSORPTION", "potiontype", "potion", "type", "name", "t", null));
	}
	
	@Override
	public boolean test(LivingEntity target) { return target.hasPotionEffect(type); }
	
}
