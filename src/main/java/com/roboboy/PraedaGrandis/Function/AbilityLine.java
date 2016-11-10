package com.roboboy.PraedaGrandis.Function;

import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Targeters.Targeter;

class AbilityLine extends FunctionLine {
	
	private final Functor ability;
	private final Targeter targeter;
	
	AbilityLine(Functor ability, Targeter targeter) {
		this.ability = ability;
		this.targeter = targeter;
	}
	
	@Override
	public void run(Target target) {
		for (Target t : targeter.getTargets(target)) {
			if (!t.isNull()) ability.run(t);
		}
		nextLine.run(target);
	}

}
