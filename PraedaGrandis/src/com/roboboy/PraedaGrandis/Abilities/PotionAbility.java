package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class PotionAbility extends Ability
{
	//final PotionEffect potionEffect;
	final PotionEffectType type;
	final int duration;
	final int amplifier;

	//LATER: Add options for force, ambient, and particles.
	public PotionAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, ConfigString args)
	{
		super(slotType, activator, targeter);
		if (args.size() >= 2)
		{
			String[] potArgs = args.get(1).split(":");
			if (potArgs.length == 3) {
				type = PotionEffectType.getByName(potArgs[0]);
				duration = Integer.parseInt(potArgs[1]);
				amplifier = Integer.parseInt(potArgs[2]);
			}
			else
			{
				//Error
				PraedaGrandis.plugin.logger.log("Improper arguments in potion ability line:", LogType.CONFIG_ERRORS);
				PraedaGrandis.plugin.logger.log("  " + args.get(1), LogType.CONFIG_ERRORS);
				PraedaGrandis.plugin.logger.log("  Has " + potArgs.length + ", requires 3.", LogType.CONFIG_ERRORS);
				type = PotionEffectType.ABSORPTION;
				duration = 0;
				amplifier = 0;
			}
			//potionEffect = new PotionEffect(PotionEffectType.getByName(potArgs[0]), Integer.parseInt(potArgs[1]), Integer.parseInt(potArgs[2]), true, false);
		}
		else
		{
			//Error
			PraedaGrandis.plugin.logger.log("Not enough arguments in potion ability line:", LogType.CONFIG_ERRORS);
			PraedaGrandis.plugin.logger.log("  " + args.getOriginalString(), LogType.CONFIG_ERRORS);
			PraedaGrandis.plugin.logger.log("  Has " + args.size() + ", requires at least 2.", LogType.CONFIG_ERRORS);
			type = PotionEffectType.ABSORPTION;
			duration = 0;
			amplifier = 0;
		}
	}
	
	@Override
	protected void execute(final Target target)
	{
		//PraedaGrandis.log("Applying " + type + " lvl" + amplifier + " to " + target.get().getType().toString() + " for " + + duration + " ticks.", LogType.TOO_MUCH_INFO);
		target.get().addPotionEffect(new PotionEffect(type, duration, amplifier, true, false), true);
	}
}
