package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.Creature;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

class IsMob extends Condition
{

	public IsMob(Targeter targeter, boolean not) {
		super(targeter, not);
	}

	@Override
	protected boolean checkThis(Target target) {
		return (target.getEntity() instanceof Creature);
	}

}
