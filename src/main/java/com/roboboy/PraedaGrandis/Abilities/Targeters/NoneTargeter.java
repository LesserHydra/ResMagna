package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.Arrays;
import java.util.List;

public class NoneTargeter extends Targeter
{
	@Override
	public List<Target> getTargets(Target currentTarget) {
		return Arrays.asList(currentTarget.target(new TargetNone()));
	}
}
