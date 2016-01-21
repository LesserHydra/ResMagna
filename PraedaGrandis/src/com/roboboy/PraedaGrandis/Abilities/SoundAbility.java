package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.AbilityArguments;

public class SoundAbility extends Ability
{
	final private Sound sound;
	final private float volume;
	final private float pitch;

	public SoundAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, AbilityArguments args) {
		super(slotType, activator, targeter);
		//TODO: Check for valid sound
		sound = Sound.valueOf(args.get("name", "", true).toUpperCase());
		volume = args.getFloat("volume", 1F, false);
		pitch = args.getFloat("pitch", 1F, false);
	}

	@Override
	protected void execute(Target target) {
		LivingEntity entityTarget = target.get();
		entityTarget.getWorld().playSound(entityTarget.getLocation(), sound, volume, pitch);
	}

}
