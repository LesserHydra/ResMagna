package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Bukkit;

class SayAbility implements Ability {
	
	private final Evaluators.ForString sayString;
	
	SayAbility(ArgumentBlock args) {
		this.sayString = args.getString(true, "",   "String", "s", null);
	}

	@Override
	public void run(Target target) {
		if (!sayString.evaluate(target)) return;
		Bukkit.broadcastMessage(sayString.get());
	}

}
