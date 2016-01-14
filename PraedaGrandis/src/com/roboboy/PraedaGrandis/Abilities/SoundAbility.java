package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class SoundAbility extends Ability
{
	final private Sound sound;
	final private float volume;
	final private float pitch;

	public SoundAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, ConfigString args)
	{
		super(slotType, activator, targeter);
		if (args.size() > 3) {
			sound = Sound.valueOf(args.get(1).toUpperCase());
			volume = Float.parseFloat(args.get(2));
			pitch = Float.parseFloat(args.get(3));
		}
		else {
			//Error
			PraedaGrandis.plugin.logger.log("Not enough arguments in sound ability line:", LogType.CONFIG_ERRORS);
			PraedaGrandis.plugin.logger.log("  " + args.getOriginalString(), LogType.CONFIG_ERRORS);
			PraedaGrandis.plugin.logger.log("  Has " + args.size() + ", requires at least 4.", LogType.CONFIG_ERRORS);
			sound = null;
			volume = 0F;
			pitch = 0F;
		}
	}

	@Override
	protected void execute(Target target)
	{
		LivingEntity entityTarget = target.get();
		entityTarget.getWorld().playSound(entityTarget.getLocation(), sound, volume, pitch);
	}

}
