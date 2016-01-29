package com.roboboy.PraedaGrandis.Abilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class AbilityFactory
{
	//(\w+)\s*(?:(\{.*\})|(\b[\w\s=+\-*/%]+\b))?\s*(@\w+(?:\([^~\n]*\))?)?\s*(?:~on(\w+)(?:\((.*)\))?:(\w+))?
	static private final Pattern abilityLinePattern = Pattern.compile("(\\w+)\\s*(?:(\\{.*\\})|(\\b[\\w\\s=+\\-*/%]+\\b))?\\s*(@\\w+(?:\\([^~\\n]*\\))?)?\\s*(?:~on(\\w+)(?:\\((.*)\\))?:(\\w+))?");
	
	public static Ability build(String abilityLine) {
		Matcher lineMatcher = abilityLinePattern.matcher(abilityLine);
		
		//Improper format
		if (!lineMatcher.matches()) {
			PraedaGrandis.plugin.logger.log("Invalid ability line format:", LogType.CONFIG_ERRORS);
			PraedaGrandis.plugin.logger.log("  " + abilityLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get ability name
		String abilityName = lineMatcher.group(1).toLowerCase();
		
		//Get ability arguments, if exist
		String argumentsString = lineMatcher.group(2);
		BlockArguments abilityArgs = new BlockArguments(argumentsString);
		
		//Get unenclosed arguments, if exist
		String variableArgs = lineMatcher.group(3);
		
		//Get Targeter
		String targeterString = lineMatcher.group(4);
		Targeter targeter = TargeterFactory.build(targeterString);
		
		//Get activator
		String activatorName = lineMatcher.group(5);
		ActivatorType actType = ActivatorType.NONE;
		if (activatorName != null) actType = ActivatorType.valueOf(activatorName.toUpperCase()); //TODO: Error handling/logging
		
		//Get activator argument
		String activatorArgument = lineMatcher.group(6);
		long timerDelay = -1;
		if (activatorArgument != null) timerDelay = Long.parseLong(activatorArgument); //TODO: Error handling/logging
		
		//Get slot type
		String slotTypeName = lineMatcher.group(7);
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
		case "heal":			return new HealAbility(slotType, actType, targeter, abilityArgs);
		case "damage":			return new DamageAbility(slotType, actType, targeter, abilityArgs);
		case "explosion":		return new ExplosionAbility(slotType, actType, targeter, abilityArgs);
		case "ignite":			return new IgniteAbility(slotType, actType, targeter, abilityArgs);
		case "disarm":			return new DisarmAbility(slotType, actType, targeter, abilityArgs);
		case "pull":			return new PullAbility(slotType, actType, targeter, abilityArgs);
		case "push":			return new PushAbility(slotType, actType, targeter, abilityArgs);
		case "fling":			return new FlingAbility(slotType, actType, targeter, abilityArgs);
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
