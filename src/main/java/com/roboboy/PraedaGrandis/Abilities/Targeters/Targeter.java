package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.List;

import com.roboboy.PraedaGrandis.Abilities.Conditions.Condition;
import com.roboboy.PraedaGrandis.PraedaGrandis;

public abstract class Targeter
{
	public abstract List<Target> getTargets(Target currentTarget);
	
	public Target getRandomTarget(Target currentTarget) {
		List<Target> targetList = getTargets(currentTarget);
		if (targetList.isEmpty()) return currentTarget.target(new TargetNone());
		if (targetList.size() == 1) return targetList.get(0);
		return targetList.get(PraedaGrandis.RANDOM_GENERATOR.nextInt(targetList.size()));
	}
	
	public boolean match(Target target, Condition condition) {
		return getTargets(target).stream()
				.allMatch(condition::test);
	}
}
