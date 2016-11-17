package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;

class SaveTargetAbility implements Ability {
	
	private final String saveName;

	SaveTargetAbility(ArgumentBlock args) {
		saveName = args.getString(true, "",		"savename", "save", "name", "sn", "n", null);
	}

	@Override
	public void run(Target target) { target.save(saveName); }

}
