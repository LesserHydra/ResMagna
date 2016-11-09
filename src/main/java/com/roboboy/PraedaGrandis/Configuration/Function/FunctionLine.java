package com.roboboy.PraedaGrandis.Configuration.Function;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;

class FunctionLine implements GrandFunction {

	GrandFunction nextLine = (target) -> {};
	
	void linkNext(GrandFunction nextLine) { this.nextLine = nextLine; }
	
	@Override
	public void run(Target target) { nextLine.run(target); }
}
