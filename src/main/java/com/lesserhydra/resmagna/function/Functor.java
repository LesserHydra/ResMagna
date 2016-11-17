package com.lesserhydra.resmagna.function;

import com.lesserhydra.resmagna.targeters.Target;

public interface Functor {
	
	Functor NONE = target -> {};
	
	void run(Target target);
	
}
