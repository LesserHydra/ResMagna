package com.lesserhydra.resmagna.targeters;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;

class SavedTargeter implements Targeter.Singleton {
	
	private final Evaluators.ForString saveName;
	
	SavedTargeter(ArgumentBlock args) {
		this.saveName = args.getString(true, "",     "savename", "save", "name", "sn", "n", null);
	}
	
	@Override
	public Target getTarget(Target currentTarget) {
		if (!saveName.evaluate(currentTarget)) return currentTarget.target(Target.none());
		return currentTarget.targetSaved(saveName.get());
	}
	
}
