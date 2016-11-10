package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Function.Functor;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;

class SaveTargetAbility implements Ability {
	
	private final String saveName;

	SaveTargetAbility(ArgumentBlock args) {
		saveName = args.getString(true, "",		"savename", "save", "name", "sn", "n", null);
	}

	@Override
	public void run(Target target) { target.save(saveName); }

}
