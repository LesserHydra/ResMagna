package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

class IsSleeping extends Condition
{
	public IsSleeping(Targeter targeter, boolean not) {
		super(targeter, not);
	}

	@Override
	protected boolean checkThis(Target target) {
		if (target.getEntity() instanceof Player)
			return ((Player)target.getEntity()).isSleeping();
		else return false;
	}
}
