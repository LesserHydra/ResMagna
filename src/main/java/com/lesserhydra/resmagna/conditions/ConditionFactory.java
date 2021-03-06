package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.configuration.GroupingParser;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionFactory {
	
	//~?(\w+)\s*(?:(?:\((\$[\d]+)\))|([\w.]+\s*[=<>]*\s*[+\-]-?[\w.]+))?
	static private final Pattern conditionLinePattern = Pattern.compile("~?(\\w+)\\s*(?:(?:\\((\\$[\\d]+)\\))|([\\w.]+\\s*[=<>]*\\s*[+\\-]?[\\w.]+))?");
	
	@Nullable
	public static Condition build(@NotNull String conditionLine) {
		conditionLine = conditionLine.toLowerCase();
		
		//Remove groupings
		GroupingParser groupParser = new GroupingParser(conditionLine);
		String simplifiedLine = groupParser.getSimplifiedString();
		
		//Match
		Matcher lineMatcher = conditionLinePattern.matcher(simplifiedLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid condition format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + conditionLine, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Simplified: " + simplifiedLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get condition name and NOT operator
		String conditionName = lineMatcher.group(1);
		boolean not = conditionLine.startsWith("~");
		
		//Get condition arguments, if exist
		String argumentsGroupID = lineMatcher.group(2);
		ArgumentBlock conditionArgs = new ArgumentBlock(groupParser.getGrouping(argumentsGroupID), conditionLine);
		
		//Get unenclosed arguments, if exist
		String variableArgsString = lineMatcher.group(3);
		
		//Construct condition by name
		Condition c = createCondition(conditionName, conditionArgs, variableArgsString);
		if (c == null) {
			GrandLogger.log("Invalid condition name: " + conditionName, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Log unused arguments
		conditionArgs.logExtra();
		
		//Return result, negated if appropriate
		return (not ? c.negate() : c);
	}
	
	private static Condition createCondition(String name, ArgumentBlock args, String varArgsString) {
		switch (name) {
		//Identity checks
		case "isnone":			return Conditions.IS_NONE;
		case "isentity":        return Conditions.IS_ENTITY;
		case "isholder":		return Conditions.IS_HOLDER;
		case "isplayer":		return Conditions.IS_PLAYER;
		case "iscreature":		return Conditions.IS_CREATURE;
		case "isageable":		return Conditions.IS_AGEABLE;
		case "isanimal":		return Conditions.IS_ANIMAL;
		case "istameable":      return Conditions.IS_TAMEABLE;
		case "ismonster":		return Conditions.IS_MONSTER;
		case "isundead":		return Conditions.IS_UNDEAD;
		case "isarthropod":		return Conditions.IS_ARTHROPOD;
		
		//Trivial entity state checks
		case "isvalid":			return Conditions.IS_VALID;
		case "hasai":		    return Conditions.HAS_AI;
		case "hasgravity":		return Conditions.HAS_GRAVITY;
		case "iscollidable":	return Conditions.IS_COLLIDABLE;
		case "isinvulnerable":	return Conditions.IS_INVULNERABLE;
		case "issilent":		return Conditions.IS_SILENT;
		case "isburning":		return Conditions.IS_BURNING;
		case "isonground":		return Conditions.IS_ON_GROUND;
		case "ismount":			return Conditions.IS_MOUNT;
		case "isriding":		return Conditions.IS_RIDING;
		case "isgliding":		return Conditions.IS_GLIDING;
		case "isglowing":		return Conditions.IS_GLOWING;
		case "isleashed":		return Conditions.IS_LEASHED;
		
		//Trivial creature state checks
		case "hastarget":       return Conditions.HAS_TARGET;
		case "isadult":         return Conditions.IS_ADULT;
		case "isageLocked":     return Conditions.IS_AGE_LOCKED;
		case "canbreed":        return Conditions.CAN_BREED;
		case "istamed":         return Conditions.IS_TAMED;
		case "iswolfsitting":   return Conditions.IS_WOLF_SITTING;
		case "iswolfangry":     return Conditions.IS_WOLF_ANGRY;
		case "iscatsitting":    return Conditions.IS_CAT_SITTING;
		case "ispigmanangry":   return Conditions.IS_PIGMAN_ANGRY;
		
		//Trivial player state checks
		case "issneaking":		return Conditions.IS_SNEAKING;
		case "issprinting":		return Conditions.IS_SPRINTING;
		case "isblocking":		return Conditions.IS_BLOCKING;
		case "ishandraised":	return Conditions.IS_HAND_RAISED;
		case "issleeping":		return Conditions.IS_SLEEPING;
		
		//Trivial location state checks
		case "israining":		return Conditions.IS_RAINING;
		case "isthundering":	return Conditions.IS_THUNDERING;
		case "issheltered":		return Conditions.IS_SHELTERED;
		case "isday":		    return Conditions.IS_DAY;
		case "isnight":		    return Conditions.IS_NIGHT;
		case "iscold":		    return Conditions.IS_COLD;
		case "ishot":		    return Conditions.IS_HOT;
		case "ismoderate":		return Conditions.IS_MODERATE;
		
		//Non-trivial state checks
		case "issame":          return new IsSame(args);
		case "isblock":			return new IsBlock(args);
		case "iswearing":		return new IsWearing(args);
		case "isholding":		return new IsHolding(args);
		case "isaffected":		return new IsAffected(args);
		//TODO: isowner
		
		//Has line of sight
		//Scoreboard
		//Permission
		//Op
		//Gamemode
		//Attribute
			
		//Other
		case "israndom":        return new IsRandom(args);
			
		//Value checks
		case "is":		        return new IsVariable(varArgsString);
		
		default:				return null;
		}
	}
	
}
