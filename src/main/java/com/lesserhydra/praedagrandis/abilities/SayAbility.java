package com.lesserhydra.praedagrandis.abilities;

import com.lesserhydra.praedagrandis.arguments.ArgumentBlock;
import com.lesserhydra.praedagrandis.targeters.Target;
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
