package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.List;

import com.roboboy.PraedaGrandis.Abilities.Conditions.Condition;
import com.roboboy.PraedaGrandis.PraedaGrandis;

public interface Targeter {
	
	List<Target> getTargets(Target currentTarget);
	
	default Target getRandomTarget(Target currentTarget) {
		List<Target> targetList = getTargets(currentTarget);
		if (targetList.isEmpty()) return currentTarget.targetNone();
		if (targetList.size() == 1) return targetList.get(0);
		return targetList.get(PraedaGrandis.RANDOM_GENERATOR.nextInt(targetList.size()));
	}
	
	default boolean match(Target target, Condition condition) {
		return getTargets(target).stream()
				.allMatch(condition::test);
	}
	
}
