package com.roboboy.PraedaGrandis.Abilities.Targeters;


public class HolderTargeter implements Targeter
{
	@Override
	public MultiTarget getTargets(Target currentTarget) {
		return new MultiTarget(currentTarget.getHolder(), currentTarget);
	}
}
