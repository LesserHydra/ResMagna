package com.lesserhydra.resmagna.configuration;

import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.function.Functor;

import java.util.function.Predicate;

/**
 * Describes a custom ability.
 * 
 * @author roboboy
 *
 */
class GrandAbility implements Functor {
	
	private final Predicate<Target> conditions;
	private final Functor thenFunction;
	private final Functor elseFunction;
	
	GrandAbility(Predicate<Target> conditions, Functor thenFunction, Functor elseFunction) {
		this.conditions = conditions;
		this.thenFunction = thenFunction;
		this.elseFunction = elseFunction;
	}

	@Override
	public void run(Target target) {
		if (conditions.test(target)) thenFunction.run(target);
		else elseFunction.run(target);
	}
	
}
