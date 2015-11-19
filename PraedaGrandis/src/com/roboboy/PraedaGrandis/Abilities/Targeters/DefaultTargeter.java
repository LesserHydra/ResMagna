package com.roboboy.PraedaGrandis.Abilities.Targeters;

public class DefaultTargeter implements Targeter
{
	@Override
	public MultiTarget getTargets(Target currentTarget) {
		return new MultiTarget(currentTarget.get(), currentTarget);
	}
}
