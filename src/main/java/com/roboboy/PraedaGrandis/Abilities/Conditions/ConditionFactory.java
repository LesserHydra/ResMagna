package com.roboboy.PraedaGrandis.Abilities.Conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GroupingParser;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ConditionFactory {
	
	//~?(\w+)\s*(?:(?:\((\$[\d]+)\))|([\w\s=<>\.]*[\w\.]))?\s*(@\w+\s*(?:\((\$[\d]+)\))?)?
	static private final Pattern conditionLinePattern = Pattern.compile("~?(\\w+)\\s*(?:(?:\\((\\$[\\d]+)\\))|([\\w\\s=<>\\.]*[\\w\\.]))?\\s*(@\\w+\\s*(?:\\((\\$[\\d]+)\\))?)?");
	
	public static Condition build(String conditionLine) {
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
		
		//Get condition name and NOT opperator
		String conditionName = lineMatcher.group(1).toLowerCase();
		boolean not = conditionLine.startsWith("~");
		
		//Get condition arguments, if exist
		String argumentsGroupID = lineMatcher.group(2);
		BlockArguments conditionArgs = new BlockArguments(groupParser.getGrouping(argumentsGroupID), conditionLine);
		
		//Get unenclosed arguments, if exist
		String variableArgsString = lineMatcher.group(3);
		
		//Get Targeter
		String targeterString = lineMatcher.group(4);
		String targeterArgumentsGroupID = lineMatcher.group(5);
		targeterString = groupParser.readdGrouping(targeterString, targeterArgumentsGroupID);
		Targeter targeter = TargeterFactory.build(targeterString);
		
		//Construct condition by name
		Condition c = createCondition(conditionName, conditionArgs, variableArgsString);
		if (c == null) {
			GrandLogger.log("Invalid condition name: " + conditionName, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Return result, negated if appropriate
		return (not ? c.negate() : c);
	}
	
	private static Condition createCondition(String name, BlockArguments args, String varArgsString) {
		switch (name) {
		//Identity checks
		case "isnone":			return Target::isNull;
		case "isholder":		return t -> t.getHolder().equals(t.getEntity());
		case "isplayer":		return t -> t.getEntity() instanceof Player;
		case "ismob":			return t -> t.getEntity() instanceof Creature;
		
		//Trivial entity state checks
		case "isvalid":			return t -> t.isEntity() && t.getEntity().isValid();
		case "isburning":		return t -> t.isEntity() && t.getEntity().getFireTicks() > 0;
		case "isonground":		return t -> t.isEntity() && t.getEntity().isOnGround();
		case "ismount":			return t -> t.isEntity() && t.getEntity().getPassenger() instanceof LivingEntity;
		case "isriding":		return t -> t.isEntity() && t.getEntity().isInsideVehicle();
		
		//Trivial player state checks
		case "issneaking":		return t -> t.isPlayer() && t.asPlayer().isSneaking();
		case "issprinting":		return t -> t.isPlayer() && t.asPlayer().isSprinting();
		case "isblocking":		return t -> t.isPlayer() && t.asPlayer().isBlocking();
		case "issleeping":		return t -> t.isPlayer() && t.asPlayer().isSleeping();
		
		//Trivial location state checks
		case "israining":		return t -> !t.isNull() && t.getLocation().getWorld().hasStorm();
		case "isthundering":	return t -> !t.isNull() && t.getLocation().getWorld().isThundering();
		case "issheltered":		return new IsSheltered();
		
		//Non-trivial state checks
		case "isblock":			return new IsBlock(args);
		case "iswearing":		return new IsWearing(args);
		case "isholding":		return new IsHolding(args);
		case "isaffected":		return new IsAffected(args);
		
		//Non-trivial value checks
		case "isvariable":		return new IsVariable(varArgsString);
		case "ishealth":		return new IsHealth(varArgsString);
		case "ishunger":		return new IsHunger(varArgsString);
		case "isexp":			return new IsExp(varArgsString);
		case "islevel":			return new IsLevel(varArgsString);
		
		default:				return null;
		}
	}
	
}
