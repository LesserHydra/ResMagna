package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import org.bukkit.Location;

class SoundAbility implements Ability {
	
	private final String sound;
	private final float volume;
	private final float pitch;

	SoundAbility(BlockArguments args) {
		sound = args.getString(true, "",	"soundname", "sound", "name", "s", "n", null);
		volume = args.getFloat(false, 1F,	"volume", "v");
		pitch = args.getFloat(false, 1F,	"pitch", "p");
	}

	@Override
	public void execute(Target target) {
		Location targetLocation = target.getLocation();
		targetLocation.getWorld().playSound(targetLocation, sound, volume, pitch);
	}

}
