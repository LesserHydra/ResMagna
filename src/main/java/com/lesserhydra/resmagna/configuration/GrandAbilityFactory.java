package com.lesserhydra.resmagna.configuration;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lesserhydra.resmagna.targeters.Targeter;
import com.lesserhydra.resmagna.targeters.TargeterFactory;
import com.lesserhydra.resmagna.function.Functor;
import com.lesserhydra.resmagna.function.LineFactory;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import org.bukkit.configuration.ConfigurationSection;
import com.lesserhydra.resmagna.conditions.Condition;
import com.lesserhydra.resmagna.conditions.ConditionFactory;
import com.lesserhydra.resmagna.targeters.Target;

class GrandAbilityFactory {
	
	//(~?\w+[^@]*(?<!\s))\s*(@.+)?
	private static final Pattern conditionLinePattern = Pattern.compile("(~?\\w+[^@]*(?<!\\s))\\s*(@.+)?");
	
	public static GrandAbility build(ConfigurationSection abilitySection) {
		Predicate<Target> conditions = x -> true;
		
		//Conditions (if)
		for (String s : abilitySection.getStringList("if")) {
			Predicate<Target> c = buildConditionLine(s);
			if (c == null) {
				//Continue error message
				GrandLogger.log("  In function: " + abilitySection.getName(), LogType.CONFIG_ERRORS);
				continue;
			}
			
			//Add condition line
			conditions = conditions.and(c);
		}
		
		//Abilities (then)
		Functor thenFunction = LineFactory.parseAndLinkLines(abilitySection.getStringList("then"));
		
		//Otherwise (else)
		Functor elseFunction = LineFactory.parseAndLinkLines(abilitySection.getStringList("else"));
		
		return new GrandAbility(conditions, thenFunction, elseFunction);
	}
	
	private static Predicate<Target> buildConditionLine(String line) {
		String l_line = line.toLowerCase();
		
		//Remove groupings
		GroupingParser groupingParser = new GroupingParser(l_line);
		String s_line = groupingParser.getSimplifiedString();
		
		//Match
		Matcher matcher = conditionLinePattern.matcher(s_line);
		if (!matcher.matches()) {
			GrandLogger.log("Invalid condition line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + line, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Simplified: " + s_line, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get component strings
		String conditionString = groupingParser.readdAllGroupings(matcher.group(1));
		String targeterString = groupingParser.readdAllGroupings(matcher.group(2));
		
		//Condition
		Condition condition = ConditionFactory.build(conditionString);
		if (condition == null) {
			//Continue error message
			GrandLogger.log("  In line: " + line, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Targeter
		Targeter targeter = TargeterFactory.build(targeterString);
		if (targeter == null) {
			//Continue error message
			GrandLogger.log("  In line: " + line, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Result
		return target -> targeter.match(target, condition);
	}

}
