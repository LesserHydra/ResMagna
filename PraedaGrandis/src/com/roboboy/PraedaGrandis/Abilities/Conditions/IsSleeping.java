package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

public class IsSleeping extends Condition
{
	public IsSleeping(Targeter targeter, boolean not) {
		super(targeter, not);
	}

	@Override
	protected boolean checkThis(Target target) {
		if (target.get() instanceof Player)
			return ((Player)target.get()).isSleeping();
		else return false;
	}
}
