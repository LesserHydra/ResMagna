package com.roboboy.PraedaGrandis.Abilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GroupingParser;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class AbilityFactory
{
	//(\w+)\s*(?:(?:\((\$[\d]+)\))|(\b[\w\s=+\-*/%]+\b))?\s*(@\w+\s*(?:\((\$[\d]+)\))?)?\s*(?:~on(\w+)(?:\((\$[\d]+)\))?:(\w+))?
	static private final Pattern abilityLinePattern = Pattern.compile("(\\w+)\\s*(?:(?:\\((\\$[\\d]+)\\))|(\\b[\\w\\s=+\\-*/%]+\\b))?\\s*(@\\w+\\s*(?:\\((\\$[\\d]+)\\))?)?\\s*(?:~on(\\w+)(?:\\((\\$[\\d]+)\\))?:(\\w+))?");
	
	public static Ability build(String abilityLine) {
		//Remove groupings
		GroupingParser groupParser = new GroupingParser(abilityLine);
		String simplifiedLine = groupParser.getSimplifiedString();
		
		//Improper format
		Matcher lineMatcher = abilityLinePattern.matcher(simplifiedLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid ability line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + abilityLine, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Simplified: " + simplifiedLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get ability name
		String abilityName = lineMatcher.group(1).toLowerCase();
		
		//Get ability arguments, if exist
		String argumentsGroupID = lineMatcher.group(2);
		BlockArguments abilityArgs = new BlockArguments(groupParser.getGrouping(argumentsGroupID));
		
		//Get unenclosed arguments, if exist
		String variableArgs = lineMatcher.group(3);
		
		//Get Targeter
		String targeterString = lineMatcher.group(4);
		String targeterArgumentsGroupID = lineMatcher.group(5);
		targeterString = groupParser.readdGrouping(targeterString, targeterArgumentsGroupID);
		Targeter targeter = TargeterFactory.build(targeterString);
		
		//Get activator
		String activatorName = lineMatcher.group(6);
		ActivatorType actType = ActivatorType.NONE;
		if (activatorName != null) actType = ActivatorType.valueOf(activatorName.toUpperCase()); //TODO: Error handling/logging
		
		//Get activator argument
		String activatorArgument = groupParser.getGrouping(lineMatcher.group(7));
		long timerDelay = -1;
		if (activatorArgument != null) timerDelay = Long.parseLong(activatorArgument); //TODO: Error handling/logging
		
		//Get slot type
		String slotTypeName = lineMatcher.group(8);
		ItemSlotType slotType = ItemSlotType.ANY;
		if (slotTypeName != null) slotType = ItemSlotType.valueOf(slotTypeName.toUpperCase()); //TODO: Error handling/logging
		
		//Construct ability by name
		Ability a = constructAbility(abilityName, slotType, actType, targeter, abilityArgs, variableArgs);
		if (timerDelay > 0) a.setTimerDelay(timerDelay);
		return a;
	}
	
	private static Ability constructAbility(String name, ItemSlotType slotType, ActivatorType actType, Targeter targeter, BlockArguments abilityArgs, String variableArgs) {
		switch (name) {
		case "delay":			return new DelayAbility(slotType, actType, targeter, abilityArgs);
		case "savetarget":		return new SaveTargetAbility(slotType, actType, targeter, abilityArgs);
		case "heal":			return new HealAbility(slotType, actType, targeter, abilityArgs);
		case "damage":			return new DamageAbility(slotType, actType, targeter, abilityArgs);
		case "explosion":		return new ExplosionAbility(slotType, actType, targeter, abilityArgs);
		case "ignite":			return new IgniteAbility(slotType, actType, targeter, abilityArgs);
		case "disarm":			return new DisarmAbility(slotType, actType, targeter, abilityArgs);
		case "force":			return new ForceAbility(slotType, actType, targeter, abilityArgs);
		case "sound":			return new SoundAbility(slotType, actType, targeter, abilityArgs);
		case "particle":		return new ParticleAbility(slotType, actType, targeter, abilityArgs);
		case "potion":			return new PotionAbility(slotType, actType, targeter, abilityArgs);
		case "teleport":		return new TeleportAbility(slotType, actType, targeter, abilityArgs);
		case "spin":			return new SpinAbility(slotType, actType, targeter, abilityArgs);
		case "swap":			return new SwapAbility(slotType, actType, targeter, abilityArgs);
		case "mount":			return new MountAbility(slotType, actType, targeter, abilityArgs);
		case "eject":			return new EjectAbility(slotType, actType, targeter);
		case "ghostblock":		return new GhostBlockAbility(slotType, actType, targeter, abilityArgs);
		case "projectile":		return new ProjectileAbility(slotType, actType, targeter, abilityArgs);
		
		case "variable":		return new VariableAbility(slotType, actType, targeter, variableArgs);
		default:				return new CustomAbility(name, slotType, actType, targeter);
		}
	}
	
}
