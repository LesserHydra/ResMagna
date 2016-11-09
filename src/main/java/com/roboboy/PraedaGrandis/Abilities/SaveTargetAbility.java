package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class SaveTargetAbility implements Ability {
	
	private final String saveName;

	SaveTargetAbility(BlockArguments args) {
		saveName = args.getString(true, "",		"savename", "save", "name", "sn", "n", null);
	}

	@Override
	public void execute(Target target) { target.save(saveName); }

}
