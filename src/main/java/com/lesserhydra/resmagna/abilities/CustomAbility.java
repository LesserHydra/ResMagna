package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.function.Functor;
import com.lesserhydra.resmagna.targeters.Target;

class CustomAbility implements Functor {
	
	private Functor function;
	
	CustomAbility(Functor function) { this.function = function; }
	
	@Override
	public void run(Target target) { function.run(target); }

}
