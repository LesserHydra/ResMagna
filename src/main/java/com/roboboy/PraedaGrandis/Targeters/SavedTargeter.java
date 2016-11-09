package com.roboboy.PraedaGrandis.Targeters;

import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

import java.util.Collections;
import java.util.List;

class SavedTargeter implements Targeter {
	
	private final String saveName;
	
	SavedTargeter(BlockArguments args) {
		saveName = args.getString(true, "",     "savename", "save", "name", "sn", "n", null);
	}
	
	@Override
	public List<Target> getTargets(Target currentTarget) {
		return Collections.singletonList(currentTarget.targetSaved(saveName));
	}
	
}
