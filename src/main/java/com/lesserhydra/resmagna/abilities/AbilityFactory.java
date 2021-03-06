package com.lesserhydra.resmagna.abilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.function.Functor;
import com.lesserhydra.resmagna.configuration.GrandAbilityHandler;
import com.lesserhydra.resmagna.configuration.GroupingParser;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import org.jetbrains.annotations.Nullable;

public class AbilityFactory {
	
	//(\w+)\s*(?:(?:\((\$[\d]+)\))|(\b[\w.\s=+\-*/%]+\b))?
	static private final Pattern abilityPattern = Pattern.compile("(\\w+)\\s*(?:(?:\\((\\$[\\d]+)\\))|(\\b[\\w.\\s=+\\-*/%]+\\b))?");
	
	@Nullable
	public static Functor build(String abilityString) {
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
		ArgumentBlock abilityArgs = new ArgumentBlock(groupParser.getGrouping(argumentsGroupID), abilityString);
		
		//Get unenclosed arguments, if exist
		String variableArgs = lineMatcher.group(3);
		
		//Construct ability by name
		Functor result = constructAbility(abilityName, abilityArgs, variableArgs);
		
		//Log unused arguments
		abilityArgs.logExtra();
		
		return result;
	}
	
	private static Functor constructAbility(String name, ArgumentBlock abilityArgs, String variableArgs) {
		switch (name) {
		//Trivial abilities
		case "eject":			return Abilities.EJECT;
		
		//Nontrivial abilities
		case "say":             return new SayAbility(abilityArgs);
		case "savetarget":		return new SaveTargetAbility(abilityArgs);
		case "damage":			return new DamageAbility(abilityArgs);
		case "explosion":		return new ExplosionAbility(abilityArgs);
		case "lightning":		return new LightningAbility(abilityArgs);
		case "firework":		return new FireworkAbility(abilityArgs);
		case "disarm":			return new DisarmAbility(abilityArgs);
		case "force":			return new ForceAbility(abilityArgs);
		case "sound":			return new SoundAbility(abilityArgs);
		case "particle":		return new ParticleAbility(abilityArgs);
		case "potion":			return new PotionAbility(abilityArgs);
		case "cloud":			return new PotionCloudAbility(abilityArgs);
		case "teleport":		return new TeleportAbility(abilityArgs);
		case "spin":			return new SpinAbility(abilityArgs);
		case "swap":			return new SwapAbility(abilityArgs);
		case "face":			return new FaceAbility(abilityArgs);
		case "mount":			return new MountAbility(abilityArgs);
		case "ghostblock":		return new GhostBlockAbility(abilityArgs);
		case "projectile":		return new ProjectileAbility(abilityArgs);
		case "beam":			return new BeamAbility(abilityArgs);
		
		//Variable set ability
		case "set":		        return new VariableAbility(variableArgs);
		
		//No built-in found, request custom
		default:				return GrandAbilityHandler.getInstance().requestFunction(name);
		}
	}
	
}
