package com.roboboy.PraedaGrandis.Function;

import com.roboboy.PraedaGrandis.Targeters.Target;

class FunctionLine implements Functor {

	Functor nextLine = (target) -> {};
	
	void linkNext(Functor nextLine) { this.nextLine = nextLine; }
	
	@Override
	public void run(Target target) { nextLine.run(target); }
}
