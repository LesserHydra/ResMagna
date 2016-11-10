package com.roboboy.PraedaGrandis.Function;

import com.roboboy.PraedaGrandis.Targeters.Target;

public interface Functor {
	
	Functor NONE = target -> {};
	
	void run(Target target);
	
}
