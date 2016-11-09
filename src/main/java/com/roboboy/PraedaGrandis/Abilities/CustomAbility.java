package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.FunctionRunner;

class CustomAbility implements Ability {
	
	private FunctionRunner grandAbility;
	
	CustomAbility(String name) {
		grandAbility = new FunctionRunner(name);
	}
	
	@Override
	public void execute(Target target) { grandAbility.run(target); }

}
