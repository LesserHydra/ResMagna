package com.roboboy.PraedaGrandis.Activator;

import com.roboboy.PraedaGrandis.Abilities.Ability;
import com.roboboy.PraedaGrandis.Abilities.AbilityFactory;
import com.roboboy.PraedaGrandis.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Configuration.GroupingParser;
import com.roboboy.PraedaGrandis.Arguments.ItemSlotType;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivatorFactory {
	
	//(.+?)(?:\s+(\@.+?))?\s+(\~.+)
	static private final Pattern seperatorPattern = Pattern.compile("(.+?)(?:\\s+(\\@.+?))?\\s+(\\~.+)");
	
	//~on(\w+)(?:\s*\((\$\d+)\))?(?:\s*\:\s*(\w+))?
	static private final Pattern activatorPattern = Pattern.compile("~on(\\w+)(?:\\s*\\((\\$\\d+)\\))?(?:\\s*\\:\\s*(\\w+))?");
	
	public static ActivatorLine build(String line) {
		//Remove groupings
		GroupingParser groupParser = new GroupingParser(line);
		String simplifiedLine = groupParser.getSimplifiedString();
		
		//Match
		Matcher seperatorMatcher = seperatorPattern.matcher(simplifiedLine);
		if (!seperatorMatcher.matches()) {
			//TODO: errors
			return null;
		}
		
		//Get components
		String abilityString = groupParser.readdAllGroupings(seperatorMatcher.group(1));
		String targeterString = groupParser.readdAllGroupings(seperatorMatcher.group(2));
		String activatorString = seperatorMatcher.group(3);
		
		//Get ability
		Ability ability = AbilityFactory.build(abilityString);
		if (ability == null) {
			GrandLogger.log("  In line: " + line, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get targeter
		Targeter targeter = TargeterFactory.build(targeterString);
		if (targeter == null) {
			GrandLogger.log("  In line: " + line, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Parse activator
		Matcher activatorMatcher = activatorPattern.matcher(activatorString);
		if (!activatorMatcher.matches()) {
			//TODO: log error
			GrandLogger.log("", LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get type
		String typeName = activatorMatcher.group(1);
		ActivatorType type = ActivatorType.fromName(typeName);
		
		//Get arguments
		//TODO: Temp
		String argString = groupParser.getGrouping(activatorMatcher.group(2));
		long timerDelay = (argString == null ? -1L : Long.parseLong(argString));
		
		//Get slot type
		String slotName = activatorMatcher.group(3);
		ItemSlotType itemSlotType = (slotName == null ? ItemSlotType.ANY : ItemSlotType.fromName(slotName));
		
		//Result
		ActivatorLine result = new ActivatorLine(ability, targeter, itemSlotType, type);
		if (timerDelay > 0) result.setTimerDelay(timerDelay);
		return result;
	}
	
}
