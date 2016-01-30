package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class TargeterFactory
{
	//@(\w+)\s*(\(.*\))?
	static private final Pattern targeterPattern = Pattern.compile("@(\\w+)\\s*(\\(.*\\))?");
	
	public static Targeter build(String targeterString) {
		//Default targeter
		if (targeterString == null) return new DefaultTargeter();
		
		//Match
		Matcher lineMatcher = targeterPattern.matcher(targeterString);
		if (!lineMatcher.matches()) {
			PraedaGrandis.plugin.logger.log("Invalid targeter format: " + targeterString, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get targeter name and NOT opperator
		String targeterName = lineMatcher.group(1).toLowerCase();
		
		//Get targeter arguments, if exist
		String argumentsString = lineMatcher.group(2);
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
		case "self":			return new DefaultTargeter();
		case "none":			return new NoneTargeter();
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
