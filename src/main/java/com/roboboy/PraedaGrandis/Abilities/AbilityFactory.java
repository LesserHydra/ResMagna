package com.roboboy.PraedaGrandis.Abilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GroupingParser;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;
import org.jetbrains.annotations.Nullable;

public class AbilityFactory {
	
	//(\w+)\s*(?:(?:\((\$[\d]+)\))|(\b[\w\s=+\-*/%]+\b))?
	static private final Pattern abilityPattern = Pattern.compile("(\\w+)\\s*(?:(?:\\((\\$[\\d]+)\\))|(\\b[\\w\\s=+\\-*/%]+\\b))?");
	
	@Nullable
	public static Ability build(String abilityString) {
		abilityString = abilityString.toLowerCase();
		
		//Remove groupings
		GroupingParser groupParser = new GroupingParser(abilityString);
		String simplifiedLine = groupParser.getSimplifiedString();
		
		//Improper format
		Matcher lineMatcher = abilityPattern.matcher(simplifiedLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid ability format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + abilityString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Simplified: " + simplifiedLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get ability name
		String abilityName = lineMatcher.group(1).toLowerCase();
		
		//Get ability arguments, if exist
		String argumentsGroupID = lineMatcher.group(2);
		BlockArguments abilityArgs = new BlockArguments(groupParser.getGrouping(argumentsGroupID), abilityString);
		
		//Get unenclosed arguments, if exist
		String variableArgs = lineMatcher.group(3);
		
		//Construct ability by name
		return constructAbility(abilityName, abilityArgs, variableArgs);
		//if (timerDelay > 0) a.setTimerDelay(timerDelay);
	}
	
	private static Ability constructAbility(String name, BlockArguments abilityArgs, String variableArgs) {
		switch (name) {
		case "savetarget":		return new SaveTargetAbility(abilityArgs);
		case "heal":			return new HealAbility(abilityArgs);
		case "damage":			return new DamageAbility(abilityArgs);
		case "explosion":		return new ExplosionAbility(abilityArgs);
		case "lightning":		return new LightningAbility(abilityArgs);
		case "firework":		return new FireworkAbility(abilityArgs);
		case "ignite":			return new IgniteAbility(abilityArgs);
		case "disarm":			return new DisarmAbility(abilityArgs);
		case "force":			return new ForceAbility(abilityArgs);
		case "sound":			return new SoundAbility(abilityArgs);
		case "particle":		return new ParticleAbility(abilityArgs);
		case "potion":			return new PotionAbility(abilityArgs);
		case "cloud":			return new PotionCloudAbility(abilityArgs);
		case "teleport":		return new TeleportAbility(abilityArgs);
		case "spin":			return new SpinAbility(abilityArgs);
		case "swap":			return new SwapAbility(abilityArgs);
		case "mount":			return new MountAbility(abilityArgs);
		case "eject":			return new EjectAbility();
		case "ghostblock":		return new GhostBlockAbility(abilityArgs);
		case "projectile":		return new ProjectileAbility(abilityArgs);
		case "beam":			return new BeamAbility(abilityArgs);
		
		case "variable":		return new VariableAbility(variableArgs);
		default:				return new CustomAbility(name);
		}
	}
	
}
