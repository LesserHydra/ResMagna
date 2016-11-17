package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Bukkit;

class SayAbility implements Ability {
	
	private final String sayString;
	
	SayAbility(ArgumentBlock args) {
		this.sayString = args.getString(true, "",   "String", "s", null).replace('&', '§');
	}

	@Override
	public void run(Target target) {
		Bukkit.broadcastMessage(this.sayString);
	}

}
