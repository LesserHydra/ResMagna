package com.roboboy.PraedaGrandis.Abilities.Targeters;

public class ActivatorTargeter implements Targeter
{
	@Override
	public MultiTarget getTargets(Target currentTarget) {
		return new MultiTarget(currentTarget.getActivator(), currentTarget);
	}
}
