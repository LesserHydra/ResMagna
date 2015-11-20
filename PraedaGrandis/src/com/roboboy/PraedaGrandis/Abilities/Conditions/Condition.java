package com.roboboy.PraedaGrandis.Abilities.Conditions;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

public abstract class Condition
{
	private final Targeter targeter;
	private final boolean not;
	
	protected Condition(Targeter targeter, boolean not)
	{
		this.targeter = targeter;
		this.not = not;
	}
	
	public final boolean check(Target target)
	{
		//boolean result = true;
		for (Target t : targeter.getTargets(target)) {
			if ( !(not ^ checkThis(t)) ) return false;
			//result = result && (not ^ checkThis(t.get())); //(not ? !result : result)
		}
		//return result;
		return true;
	}
	
	protected abstract boolean checkThis(Target target);
}
