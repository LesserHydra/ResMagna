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
		
		sound = args.getEnum(true, Sound.UI_BUTTON_CLICK,	"soundname", "sound", "name", "s", "n", null);
		volume = args.getFloat(false, 1F,					"volume", "v");
		pitch = args.getFloat(false, 1F,					"pitch", "p");
	}

	@Override
	protected void execute(Target target) {
		Location targetLocation = target.getLocation();
		targetLocation.getWorld().playSound(targetLocation, sound, volume, pitch);
	}

}
