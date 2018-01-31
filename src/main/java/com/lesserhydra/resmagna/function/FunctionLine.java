package com.lesserhydra.resmagna.function;

import com.lesserhydra.resmagna.targeters.Target;

class FunctionLine implements Functor {

	Functor nextLine = (target) -> {};
	
	void linkNext(Functor nextLine) { this.nextLine = nextLine; }
	
	@Override
	public void run(Target target) { nextLine.run(target); }
}
