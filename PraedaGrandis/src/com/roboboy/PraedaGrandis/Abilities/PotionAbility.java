package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.AbilityArguments;

public class PotionAbility extends Ability
{
	//final PotionEffect potionEffect;
	final PotionEffectType type;
	final int duration;
	final int amplifier;

	//LATER: Add options for force, ambient, and particles.
	public PotionAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, AbilityArguments args)
	{
		super(slotType, activator, targeter);
		
		//TODO: Verify name
		type = PotionEffectType.getByName(args.get("name", "ABSORPTION", true));
		duration = args.getInteger("duration", 0, true);
		amplifier = args.getInteger("amplifier", 0, false);
	}
	
	@Override
	protected void execute(final Target target)
	{
		//PraedaGrandis.log("Applying " + type + " lvl" + amplifier + " to " + target.get().getType().toString() + " for " + + duration + " ticks.", LogType.TOO_MUCH_INFO);
		target.get().addPotionEffect(new PotionEffect(type, duration, amplifier, true, false), true);
	}
}
