package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

class IsSprinting extends Condition
{
	protected IsSprinting(Targeter targeter, boolean not) {
		super(targeter, not);
	}

	@Override
	public boolean checkThis(Target target)
	{
		if (target.getEntity() instanceof Player)
			return ((Player)target.getEntity()).isSprinting();
		else return false;
	}

}
