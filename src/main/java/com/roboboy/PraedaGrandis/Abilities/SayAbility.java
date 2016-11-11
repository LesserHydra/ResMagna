package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import com.roboboy.PraedaGrandis.Targeters.Target;
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
