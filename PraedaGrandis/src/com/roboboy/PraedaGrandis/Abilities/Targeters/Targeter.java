package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.List;
import com.roboboy.PraedaGrandis.PraedaGrandis;

public abstract class Targeter
{
	public abstract List<Target> getTargets(Target currentTarget);
	
	public Target getRandomTarget(Target currentTarget) {
		List<Target> targetList = getTargets(currentTarget);
		if (targetList.isEmpty()) return null;
		if (targetList.size() == 1) return targetList.get(0);
		return targetList.get(PraedaGrandis.RANDOM_GENERATOR.nextInt(targetList.size()));
	}
}
