package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.Arrays;
import java.util.List;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

public class SavedTargeter extends Targeter
{
	private final String saveName;
	
	public SavedTargeter(BlockArguments args) {
		saveName = args.getString(true, "",		"savename", "save", "name", "sn", "n", null);
	}
	
	@Override
	public List<Target> getTargets(Target currentTarget) {
		return Arrays.asList(currentTarget.targetSaved(saveName));
	}
}
