package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import org.bukkit.Location;

class SoundAbility implements Ability.ForLocation {
	
	private final String sound;
	private final float volume;
	private final float pitch;

	SoundAbility(ArgumentBlock args) {
		sound = args.getString(true, "",	"soundname", "sound", "name", "s", "n", null);
		volume = args.getFloat(false, 1F,	"volume", "v");
		pitch = args.getFloat(false, 1F,	"pitch", "p");
	}

	@Override
	public void run(Location target) {
		target.getWorld().playSound(target, sound, volume, pitch);
	}

}
