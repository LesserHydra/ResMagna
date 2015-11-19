package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

public class IsSneaking extends Condition
{
	
	protected IsSneaking(Targeter targeter, boolean not) {
		super(targeter, not);
	}

	@Override
	public boolean checkThis(LivingEntity target)
	{
		if (target instanceof Player)
			return ((Player)target).isSneaking();
		else return false;
	}

}
