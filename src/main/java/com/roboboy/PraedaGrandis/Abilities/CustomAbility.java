package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Function.Functor;
import com.roboboy.PraedaGrandis.Targeters.Target;

class CustomAbility implements Functor {
	
	private Functor function;
	
	CustomAbility(Functor function) { this.function = function; }
	
	@Override
	public void run(Target target) { function.run(target); }

}
