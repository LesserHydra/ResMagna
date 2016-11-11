package com.lesserhydra.praedagrandis.abilities;

import com.lesserhydra.praedagrandis.targeters.Target;
import com.lesserhydra.praedagrandis.arguments.ArgumentBlock;

class SaveTargetAbility implements Ability {
	
	private final String saveName;

	SaveTargetAbility(ArgumentBlock args) {
		saveName = args.getString(true, "",		"savename", "save", "name", "sn", "n", null);
	}

	@Override
	public void run(Target target) { target.save(saveName); }

}
