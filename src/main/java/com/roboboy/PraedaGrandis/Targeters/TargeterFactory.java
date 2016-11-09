package com.roboboy.PraedaGrandis.Targeters;

import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GroupingParser;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TargeterFactory {
	
	//@(\w+)\s*(?:\((\$[\d]+)\))?
	static private final Pattern targeterPattern = Pattern.compile("@(\\w+)\\s*(?:\\((\\$[\\d]+)\\))?");
	
	public static Targeter build(String targeterString) {
		//Default targeter
		if (targeterString == null) return Targeters.CURRENT;
		
		//Pull out groupings
		GroupingParser groupingParser = new GroupingParser(targeterString);
		String simplifiedString = groupingParser.getSimplifiedString();
		
		//Match
		Matcher lineMatcher = targeterPattern.matcher(simplifiedString);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid targeter format: " + targeterString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Simplified: " + simplifiedString, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get targeter name
		String targeterName = lineMatcher.group(1).toLowerCase();
		
		//Get targeter arguments, if exist
		String argumentsString = groupingParser.getGrouping(lineMatcher.group(2));
		BlockArguments targeterArgs = new BlockArguments(argumentsString, targeterString);
		
		//Construct targeter by name
		Targeter result = constructTargeter(targeterName, targeterArgs);
		if (result == null) {
			GrandLogger.log("Invalid condition name: " + targeterName, LogType.CONFIG_ERRORS);
		}
		return result;
	}
	
	private static Targeter constructTargeter(String targeterName, BlockArguments targeterArgs) {
		switch (targeterName) {
		//Single-target
		case "none":			return Targeters.NONE;
		case "current":			return Targeters.CURRENT;
		case "holder":			return Targeters.HOLDER;
		case "activator":		return Targeters.ACTIVATOR;
		case "mount":			return Targeters.MOUNT;
		case "rider":			return Targeters.RIDER;
		case "location":		return new LocationTargeter(targeterArgs);
		case "saved":			return new SavedTargeter(targeterArgs);
		
		//Multi-target
		case "onlineplayers":	return Targeters.ONLINE_PLAYERS;
		case "boundingbox":		return new BoundingBoxTargeter(targeterArgs);
		
		default:				return null;
		}
	}

}
