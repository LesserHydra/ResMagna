package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Function.Functor;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import org.bukkit.Location;

class SoundAbility implements Functor {
	
	private final String sound;
	private final float volume;
	private final float pitch;

	SoundAbility(ArgumentBlock args) {
		sound = args.getString(true, "",	"soundname", "sound", "name", "s", "n", null);
		volume = args.getFloat(false, 1F,	"volume", "v");
		pitch = args.getFloat(false, 1F,	"pitch", "p");
	}

	@Override
	public void run(Target target) {
		Location targetLocation = target.asLocation();
		targetLocation.getWorld().playSound(targetLocation, sound, volume, pitch);
	}

}
