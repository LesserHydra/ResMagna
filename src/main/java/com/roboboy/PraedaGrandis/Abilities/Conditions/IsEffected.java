package com.roboboy.PraedaGrandis.Abilities.Conditions;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import org.bukkit.potion.PotionEffectType;

class IsEffected extends Condition {
	
	private final PotionEffectType type;
	
	IsEffected(Targeter targeter, boolean not, BlockArguments args) {
		super(targeter, not);
		//TODO: temp
		type = PotionEffectType.getByName(args.getString(true, "ABSORPTION",	"potiontype", "potion", "type", "name", "t", null));
	}
	
	@Override
	protected boolean checkThis(Target target) {
		return target.isEntity() && target.getEntity().hasPotionEffect(type);
	}
	
}
