package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class PotionAbility extends Ability
{
	//final PotionEffect potionEffect;
	private final PotionEffectType type;
	private final int duration;
	private final int amplifier;

	//LATER: Add options for force, ambient, and particles.
	public PotionAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args)
	{
		super(slotType, activator, targeter);
		
		//TODO: type = args.getPotionEffectType("name", PotionEffectType.ABSORPTION, true);
		type = PotionEffectType.getByName(args.getString(true, "ABSORPTION",	"potiontype", "potion", "type", "name", "t", null));
		
		duration = args.getInteger(false, 600,		"duration", "ticks", "d");
		amplifier = args.getInteger(false, 0,		"amplifier", "level", "amp", "a");
	}
	
	@Override
	protected void execute(Target target)
	{
		//PraedaGrandis.log("Applying " + type + " lvl" + amplifier + " to " + target.get().getType().toString() + " for " + + duration + " ticks.", LogType.TOO_MUCH_INFO);
		target.getEntity().addPotionEffect(new PotionEffect(type, duration, amplifier, true, false), true);
	}
}
