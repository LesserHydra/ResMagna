package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
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
		sound = Sound.valueOf(args.get("name", "CLICK", true).toUpperCase());
		volume = args.getFloat("volume", 1F, false);
		pitch = args.getFloat("pitch", 1F, false);
	}

	@Override
	protected void execute(Target target) {
		LivingEntity entityTarget = target.get();
		entityTarget.getWorld().playSound(entityTarget.getLocation(), sound, volume, pitch);
	}

}
