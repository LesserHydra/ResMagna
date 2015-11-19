package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

public class IsPlayer extends Condition
{

	protected IsPlayer(Targeter targeter, boolean not) {
		super(targeter, not);
	}

	@Override
	protected boolean checkThis(LivingEntity target) {
		return (target instanceof Player);
	}

}
