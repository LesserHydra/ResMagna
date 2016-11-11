package com.lesserhydra.praedagrandis.function;

import com.lesserhydra.praedagrandis.targeters.Target;

public interface Functor {
	
	Functor NONE = target -> {};
	
	void run(Target target);
	
}
