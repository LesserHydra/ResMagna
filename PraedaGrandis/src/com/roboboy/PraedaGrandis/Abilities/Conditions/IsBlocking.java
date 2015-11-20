package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

public class IsBlocking extends Condition
{

	public IsBlocking(Targeter targeter, boolean not){
		super(targeter, not);
	}

	@Override
	protected boolean checkThis(Target target){
		if (target.get() instanceof Player)
			return ((Player)target.get()).isBlocking();
		else return false;
	}

}
