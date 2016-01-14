package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;
import com.roboboy.PraedaGrandis.Logging.LogType;

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
		Ability a = constructAbility(abilityName, slotType, actType, targeter, args);
		
		if (timerDelay > 0) a.setTimerDelay(timerDelay);
		
		if (a != null) {
			PraedaGrandis.plugin.logger.log(s, LogType.CONFIG_PARSING);
			PraedaGrandis.plugin.logger.log("\tAbilityType: " + abilityName, LogType.CONFIG_PARSING);
			PraedaGrandis.plugin.logger.log("\tTargeterType: " + targeter.getClass().getSimpleName(), LogType.CONFIG_PARSING);
			PraedaGrandis.plugin.logger.log("\tSlotType: " + slotType.name(), LogType.CONFIG_PARSING);
			PraedaGrandis.plugin.logger.log("\tActivatorType: " + actType.name(), LogType.CONFIG_PARSING);
			PraedaGrandis.plugin.logger.log("\tTimerDelay: " + timerDelay, LogType.CONFIG_PARSING);
		}
		
		return a;
	}
	
	private static Ability constructAbility(String name, ItemSlotType slotType, ActivatorType actType, Targeter targeter, ConfigString args)
	{
		switch (name) {
		case "custom":			return new CustomAbility(slotType, actType, targeter, args);
		case "delay":			return new DelayAbility(slotType, actType, targeter, args);
		case "variable":		return new VariableAbility(slotType, actType, targeter, args);
		case "heal":			return new HealAbility(slotType, actType, targeter, args);
		case "damage":			return new DamageAbility(slotType, actType, targeter, args);
		case "pull":			return new PullAbility(slotType, actType, targeter, args);
		case "push":			return new PushAbility(slotType, actType, targeter, args);
		case "fling":			return new FlingAbility(slotType, actType, targeter, args);
		case "sound":			return new SoundAbility(slotType, actType, targeter, args);
		case "particle":		return new ParticleAbility(slotType, actType, targeter, args);
		case "potion":			return new PotionAbility(slotType, actType, targeter, args);
		case "teleport":		return new TeleportAbility(slotType, actType, targeter, args);
		case "swapholder":		return new SwapHolderAbility(slotType, actType, targeter, args);
		case "swapactivator":	return new SwapActivatorAbility(slotType, actType, targeter, args);
		case "mountholder":		return new MountHolderAbility(slotType, actType, targeter);
		case "holdermount":		return new HolderMountAbility(slotType, actType, targeter);
		case "eject":			return new EjectAbility(slotType, actType, targeter);
		case "ghostblock":		return new GhostBlockAbility(slotType, actType, targeter, args);
		
		default:				return null;
		}
	}
}
