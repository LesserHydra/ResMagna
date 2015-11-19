package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

public class IsNull extends Condition
{

	protected IsNull(Targeter targeter, boolean not) {
		super(targeter, not);
	}

	@Override
	protected boolean checkThis(LivingEntity target) {
		return (target == null);
	}

}
