package com.roboboy.PraedaGrandis.Configuration.Function;

import com.roboboy.PraedaGrandis.Abilities.Ability;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;

class AbilityLine extends FunctionLine {
	
	private final Ability ability;
	private final Targeter targeter;
	
	AbilityLine(Ability ability, Targeter targeter) {
		this.ability = ability;
		this.targeter = targeter;
	}
	
	@Override
	public void run(Target target) {
		for (Target t : targeter.getTargets(target)) {
			if (!t.isNull()) ability.execute(t);
		}
		nextLine.run(target);
	}

}
