package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

public class IsSneaking extends Condition
{
	
	protected IsSneaking(Targeter targeter, boolean not) {
		super(targeter, not);
	}

	@Override
	public boolean checkThis(Target target)
	{
		if (target.get() instanceof Player)
			return ((Player)target.get()).isSneaking();
		else return false;
	}

}
