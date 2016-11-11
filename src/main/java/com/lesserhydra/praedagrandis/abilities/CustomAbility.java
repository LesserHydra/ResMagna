package com.lesserhydra.praedagrandis.abilities;

import com.lesserhydra.praedagrandis.function.Functor;
import com.lesserhydra.praedagrandis.targeters.Target;

class CustomAbility implements Functor {
	
	private Functor function;
	
	CustomAbility(Functor function) { this.function = function; }
	
	@Override
	public void run(Target target) { function.run(target); }

}
