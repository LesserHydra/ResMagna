package com.lesserhydra.resmagna.targeters;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.arguments.GrandLocation;

class LocationTargeter implements Targeter.Singleton {
	
	private final Evaluators.ForLocation grandLocation;
	
	LocationTargeter(ArgumentBlock args) {
		this.grandLocation = args.getLocation(true, GrandLocation.CURRENT, "location", "loc", "l", null);
	}
	
	@Override
	public Target getTarget(Target currentTarget) {
		if (!grandLocation.evaluate(currentTarget)) return currentTarget.target(Target.none());
		return currentTarget.target(Target.from(grandLocation.get()));
	}

}
