package com.roboboy.PraedaGrandis.Configuration;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.Function.GrandFunction;

import java.util.function.Predicate;

/**
 * Describes a custom ability.
 * 
 * @author roboboy
 *
 */
public class GrandAbility {
	
	private final Predicate<Target> conditions;
	private final GrandFunction thenFunction;
	private final GrandFunction elseFunction;
	

	GrandAbility(Predicate<Target> conditions, GrandFunction thenFunction, GrandFunction elseFunction) {
		this.conditions = conditions;
		this.thenFunction = thenFunction;
		this.elseFunction = elseFunction;
	}

	public void run(Target target) {
		if (conditions.test(target)) thenFunction.run(target);
		else elseFunction.run(target);
	}
	
}
