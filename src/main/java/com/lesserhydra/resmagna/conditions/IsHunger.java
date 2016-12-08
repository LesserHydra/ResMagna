package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.arguments.VariableConditional;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.util.StringTools;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IsHunger implements Condition.ForPlayer {
	
	//([=<>]+)\s*([\w\.]+)
	private static final Pattern isLinePattern = Pattern.compile("([=<>]+)\\s*([\\w]+)");
	
	private final VariableConditional	conditional;
	private final int					number;

	IsHunger(String argLine) {
		//Match
		Matcher lineMatcher = isLinePattern.matcher(argLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid hunger condition line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + argLine, LogType.CONFIG_ERRORS);
			conditional = VariableConditional.EQUAL;
			number = 0;
			return;
		}
		
		//Get operator
		conditional = VariableConditional.fromSymbol(lineMatcher.group(1));
		
		//Operand may be an integer or the name of a variable
		String operand = lineMatcher.group(2);
		if (!StringTools.isInteger(operand)) {
			GrandLogger.log("Invalid hunger condition operand:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + argLine, LogType.CONFIG_ERRORS);
			number = 0;
			return;
		}
		number = Integer.parseInt(operand);
	}

	@Override
	public boolean test(Player target) {
		return conditional.check(target.getFoodLevel(), number);
	}

}