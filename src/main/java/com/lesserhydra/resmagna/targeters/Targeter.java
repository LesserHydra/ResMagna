package com.lesserhydra.resmagna.targeters;

import com.lesserhydra.resmagna.ResMagna;
import com.lesserhydra.resmagna.conditions.Condition;

import java.util.Collections;
import java.util.List;

public interface Targeter {
	
	List<Target> getTargets(Target currentTarget);
	
	default Target getRandomTarget(Target currentTarget) {
		List<Target> targetList = getTargets(currentTarget);
		if (targetList.isEmpty()) return currentTarget.targetNone();
		if (targetList.size() == 1) return targetList.get(0);
		return targetList.get(ResMagna.RANDOM_GENERATOR.nextInt(targetList.size()));
	}
	
	default boolean match(Target currentTarget, Condition condition) {
		return getTargets(currentTarget).stream()
				.allMatch(condition::test);
	}
	
	/**
	 * Represents a Targeter that always returns exactly one Target.
	 */
	interface Singleton extends Targeter {
		Target getTarget(Target currentTarget);
		
		@Override
		default List<Target> getTargets(Target currentTarget) {
			return Collections.singletonList(getTarget(currentTarget));
		}
		
		@Override
		default Target getRandomTarget(Target currentTarget) {
			return getTarget(currentTarget);
		}
		
		@Override
		default boolean match(Target currentTarget, Condition condition) {
			return condition.test(getTarget(currentTarget));
		}
	}
	
}
