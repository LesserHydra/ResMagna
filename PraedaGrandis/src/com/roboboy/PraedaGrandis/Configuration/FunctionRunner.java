package com.roboboy.PraedaGrandis.Configuration;

import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;

public class FunctionRunner
{	
	private GrandAbility function;
	
	public FunctionRunner(String functionName) {
		PraedaGrandis.plugin.abilityHandler.requestFunction(this, functionName);
	}
	
	public void run(Target target) {
		if (function == null) return;
		function.run(target);
	}
	
	public void returnRequest(GrandAbility function) {
		this.function = function;
	}
}
