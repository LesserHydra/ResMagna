package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.variables.ValueConstruct;
import com.lesserhydra.resmagna.variables.ValueConstructs;
import org.bukkit.Bukkit;

class SayAbility implements Ability {
	
	private final ValueConstruct sayString;
	
	SayAbility(ArgumentBlock args) {
		this.sayString = args.getValue(true, ValueConstructs.NONE,   "String", "s", null);
	}

	@Override
	public void run(Target target) {
		Bukkit.broadcastMessage(sayString.get(target).asString());
	}

}
