package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GroupingParser;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class TargeterFactory
{
	//@(\w+)\s*(?:\((\$[\d]+)\))?
	static private final Pattern targeterPattern = Pattern.compile("@(\\w+)\\s*(?:\\((\\$[\\d]+)\\))?");
	
	public static Targeter build(String targeterString) {
		//Default targeter
		if (targeterString == null) return new CurrentTargeter();
		
		//Pull out groupings
		GroupingParser groupingParser = new GroupingParser(targeterString);
		String simplifiedString = groupingParser.getSimplifiedString();
		
		//Match
		Matcher lineMatcher = targeterPattern.matcher(simplifiedString);
		if (!lineMatcher.matches()) {
			PraedaGrandis.plugin.logger.log("Invalid targeter format: " + targeterString, LogType.CONFIG_ERRORS);
			PraedaGrandis.plugin.logger.log("  Simplified: " + simplifiedString, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get targeter name
		String targeterName = lineMatcher.group(1).toLowerCase();
		
		//Get targeter arguments, if exist
		String argumentsString = groupingParser.getGrouping(lineMatcher.group(2));
		BlockArguments targeterArgs = new BlockArguments(argumentsString);
		
		//Construct targeter by name
		Targeter result = constructTargeter(targeterName, targeterArgs);
		if (result == null) {
			PraedaGrandis.plugin.logger.log("Invalid condition name: " + targeterName, LogType.CONFIG_ERRORS);
		}
		return result;
	}
	
	private static Targeter constructTargeter(String targeterName, BlockArguments targeterArgs)
	{
		switch (targeterName) {
		case "none":			return new NoneTargeter();
		case "current":			return new CurrentTargeter();
		case "saved":			return new SavedTargeter(targeterArgs);
		case "holder":			return new HolderTargeter();
		case "activator":		return new ActivatorTargeter();
		case "mount":			return new MountTargeter();
		case "rider":			return new RiderTargeter();
		
		case "onlineplayers":	return new OnlinePlayersTargeter();
		case "boundingbox":		return new BoundingBoxTargeter(targeterArgs);
		
		default:				return null;
		}
	}

}
