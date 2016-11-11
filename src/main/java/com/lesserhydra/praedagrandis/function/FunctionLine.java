package com.lesserhydra.praedagrandis.function;

import com.lesserhydra.praedagrandis.targeters.Target;

class FunctionLine implements Functor {

	Functor nextLine = (target) -> {};
	
	void linkNext(Functor nextLine) { this.nextLine = nextLine; }
	
	@Override
	public void run(Target target) { nextLine.run(target); }
}
