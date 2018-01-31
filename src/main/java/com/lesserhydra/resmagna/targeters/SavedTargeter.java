package com.lesserhydra.resmagna.targeters;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;

class SavedTargeter implements Targeter.Singleton {
	
	private final String saveName;
	
	SavedTargeter(ArgumentBlock args) {
		saveName = args.getString(true, "",     "savename", "save", "name", "sn", "n", null);
	}
	
	@Override
	public Target getTarget(Target currentTarget) { return currentTarget.targetSaved(saveName);}
	
}
