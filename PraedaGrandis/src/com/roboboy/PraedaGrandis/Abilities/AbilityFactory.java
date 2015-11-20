package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;

public class AbilityFactory
{
	public static Ability build(String s)
	{
		ConfigString args = new ConfigString(s);
		
		//Cannot be empty
		if (args.size() == 0) return null;
		
		//Get name
		String abilityName = args.get(0);
		
		//Targeter
		Targeter targeter;
		int targeterIndex = args.contains(TargeterFactory.PREFIX);
		if (targeterIndex != -1) {
			targeter = TargeterFactory.build(args.get(targeterIndex));
		}
		else {
			targeter = TargeterFactory.getDefault();
		}
		
		//Activator & Slot
		ActivatorType actType;
		long timerDelay = -1;
		ItemSlotType slotType;
		int actIndex = args.contains("~on");
		if (actIndex != -1) {
			String[] actStrings = args.get(actIndex).replace("~on", "").toUpperCase().split(":");
			String[] actTypeStrings = actStrings[0].replace(")", "").split("\\("); //Seperate timer argument (special case)
			
			actType = ActivatorType.valueOf(actTypeStrings[0]); //Activator type
			if (actTypeStrings.length == 2) timerDelay = Long.parseLong(actTypeStrings[1]); //Timer delay (special case)
			slotType = (actStrings.length == 2 ? ItemSlotType.valueOf(actStrings[1]) : ItemSlotType.ANY); //Spot type
		}
		else {
			actType = ActivatorType.NONE;
			slotType = ItemSlotType.ANY;
		}
		
		//Choose proper ability by name
		Ability a = null;
		switch (abilityName)
		{
		case "custom": a = new CustomAbility(slotType, actType, targeter, args);
		break;
		case "variable": a = new VariableAbility(slotType, actType, targeter, args);
		break;
		case "heal": a = new HealAbility(slotType, actType, targeter, args);
		break;
		case "damage": a = new DamageAbility(slotType, actType, targeter, args);
		break;
		case "sound": a = new SoundAbility(slotType, actType, targeter, args);
		break;
		case "particle": a = new ParticleAbility(slotType, actType, targeter, args);
		break;
		case "potion": a = new PotionAbility(slotType, actType, targeter, args);
		break;
		case "ghostblock": a = new GhostBlockAbility(slotType, actType, targeter, args);
		break;
		}
		
		if (timerDelay > 0) a.setTimerDelay(timerDelay);
		
		if (a != null) {
			PraedaGrandis.plugin.getLogger().info(s);
			PraedaGrandis.plugin.getLogger().info("\tAbilityType: " + abilityName);
			PraedaGrandis.plugin.getLogger().info("\tTargeterType: " + targeter.getClass().getSimpleName());
			PraedaGrandis.plugin.getLogger().info("\tSlotType: " + slotType.name());
			PraedaGrandis.plugin.getLogger().info("\tActivatorType: " + actType.name());
			PraedaGrandis.plugin.getLogger().info("\tTimerDelay: " + timerDelay);
			
			if (args.size() != 0) {
				PraedaGrandis.plugin.getLogger().info("\tArguments:");
				for (String argString : args) {
					PraedaGrandis.plugin.getLogger().info("\t- " + argString);
				}
			}
		}
		
		return a;
	}
}
