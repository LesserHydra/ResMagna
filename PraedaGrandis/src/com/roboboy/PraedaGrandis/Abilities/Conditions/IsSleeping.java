package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

public class IsSleeping extends Condition
{
	public IsSleeping(Targeter targeter, boolean not) {
		super(targeter, not);
	}

	@Override
	protected boolean checkThis(LivingEntity target) {
		if (target instanceof Player)
			return ((Player)target).isSleeping();
		else return false;
	}
}
