package com.roboboy.PraedaGrandis.Conditions;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import com.roboboy.PraedaGrandis.Configuration.GroupingParser;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionFactory {
	
	//~?(\w+)\s*(?:(?:\((\$[\d]+)\))|([\w\s=<>.]*[\w.]))?
	static private final Pattern conditionLinePattern = Pattern.compile("~?(\\w+)\\s*(?:(?:\\((\\$[\\d]+)\\))|([\\w\\s=<>.]*[\\w.]))?");
	
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
		
		//Non-trivial state checks
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
	
		//Non-trivial value checks
		case "isvariable":		return new IsVariable(varArgsString);
		case "ishealth":		return Conditions.isHealth(varArgsString);
		case "ishunger":		return Conditions.isHunger(varArgsString);
		case "isexp":			return Conditions.isExp(varArgsString);
		case "islevel":			return Conditions.isLevel(varArgsString);
		
		default:				return null;
		}
	}
	
}
