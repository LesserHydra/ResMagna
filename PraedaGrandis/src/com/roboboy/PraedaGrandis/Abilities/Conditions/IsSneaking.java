package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

class IsSneaking extends Condition
{
	
	protected IsSneaking(Targeter targeter, boolean not) {
		super(targeter, not);
	}

	@Override
	public boolean checkThis(Target target)
	{
		if (target.getEntity() instanceof Player)
			return ((Player)target.getEntity()).isSneaking();
		else return false;
	}

}
