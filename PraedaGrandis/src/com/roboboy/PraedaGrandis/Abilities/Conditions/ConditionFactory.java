package com.roboboy.PraedaGrandis.Abilities.Conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class ConditionFactory
{
	//~?(\w+)\s*(?:(\{.*\})|(\b[\w\s=<>]+\b))?\s*(?:@(\w+)(?:\((.*)\))?)?
	static private final Pattern conditionLinePattern = Pattern.compile("~?(\\w+)\\s*(?:(\\{.*\\})|(\\b[\\w\\s=<>]+\\b))?\\s*(?:@(\\w+)(?:\\((.*)\\))?)?");
	
	public static Condition build(String conditionLine) {
		//Match
		Matcher lineMatcher = conditionLinePattern.matcher(conditionLine);
		if (!lineMatcher.matches()) {
			PraedaGrandis.plugin.logger.log("Invalid condition line format:", LogType.CONFIG_ERRORS);
			PraedaGrandis.plugin.logger.log("  " + conditionLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get condition name and NOT opperator
		String conditionName = lineMatcher.group(1);
		boolean not = conditionLine.startsWith("~");
		
		//Get condition arguments, if exist
		String argumentsString = lineMatcher.group(2);
		BlockArguments conditionArgs = new BlockArguments(argumentsString);
		
		//Get unenclosed arguments, if exist
		String variableArgsString = lineMatcher.group(3);
		
		//Get Targeter
		String targeterName = lineMatcher.group(4);
		if (targeterName == null) targeterName = "default";
		String targeterArgument = lineMatcher.group(5);
		Targeter targeter = TargeterFactory.build(targeterName, targeterArgument);
		
		//Construct condition by name
		Condition c = createCondition(conditionName, targeter, not, conditionArgs, variableArgsString);
		if (c == null) {
			PraedaGrandis.plugin.logger.log("Invalid condition name: " + conditionName, LogType.CONFIG_ERRORS);
		}
		return c;
	}
	
	private static Condition createCondition(String name, Targeter targeter, boolean not, BlockArguments args, String varArgsString)
	{
		switch (name) {
		case "isnull":			return new IsNull(targeter, not);
		case "isholder":		return new IsHolder(targeter, not);
		case "isplayer":		return new IsPlayer(targeter, not);
		case "ismob":			return new IsMob(targeter, not);
		case "issneaking":		return new IsSneaking(targeter, not);
		case "issprinting":		return new IsSprinting(targeter, not);
		case "isonground":		return new IsOnGround(targeter, not);
		case "issheltered":		return new IsSheltered(targeter, not);
		case "isblocking":		return new IsBlocking(targeter, not);
		case "issleeping":		return new IsSleeping(targeter, not);
		case "israining":		return new IsRaining(targeter, not);
		case "isthundering":	return new IsThundering(targeter, not);
		case "ismount":			return new IsMount(targeter, not);
		case "isriding":		return new IsRiding(targeter, not);
		
		case "isblock":			return new IsBlock(targeter, not, args);
		case "iswearing":		return new IsWearing(targeter, not, args);
		case "isholding":		return new IsHolding(targeter, not, args);
		
		case "isvariable":		return new IsVariable(targeter, not, varArgsString);
		case "ishealth":		return new IsHealth(targeter, not, varArgsString);
		case "ishunger":		return new IsHunger(targeter, not, varArgsString);
		case "isexp":			return new IsExp(targeter, not, varArgsString);
		case "islevel":			return new IsLevel(targeter, not, varArgsString);
		
		default:				return null; //TODO: Make a default condition (always false?)
		}
	}
	
}
