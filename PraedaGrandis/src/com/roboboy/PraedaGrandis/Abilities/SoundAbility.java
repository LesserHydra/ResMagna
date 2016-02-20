package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.Sound;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class SoundAbility extends Ability
{
	private final Sound sound;
	private final float volume;
	private final float pitch;

	public SoundAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		//TODO: Check for valid sound
		sound = args.getEnum("name", Sound.CLICK, true);
		volume = args.getFloat("volume", 1F, false);
		pitch = args.getFloat("pitch", 1F, false);
	}

	@Override
	protected void execute(Target target) {
		Location targetLocation = target.getLocation();
		targetLocation.getWorld().playSound(targetLocation, sound, volume, pitch);
	}

}
