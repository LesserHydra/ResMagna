package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

public class IsMob extends Condition
{

	protected IsMob(Targeter targeter, boolean not) {
		super(targeter, not);
	}

	@Override
	protected boolean checkThis(LivingEntity target) {
		return (target instanceof Creature);
	}

}
