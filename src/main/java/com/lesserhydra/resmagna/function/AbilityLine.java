package com.lesserhydra.resmagna.function;

import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeter;

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
