package com.roboboy.PraedaGrandis.Conditions;

import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import org.bukkit.potion.PotionEffectType;

class IsAffected implements Condition {
	
	private final PotionEffectType type;
	
	IsAffected(ArgumentBlock args) {
		//TODO: temp
		type = PotionEffectType.getByName(args.getString(true, "ABSORPTION", "potiontype", "potion", "type", "name", "t", null));
	}
	
	@Override
	public boolean test(Target target) { return target.isEntity() && target.asEntity().hasPotionEffect(type); }
	
}
