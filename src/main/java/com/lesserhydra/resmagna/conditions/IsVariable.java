package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.arguments.VariableConditional;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.VariableHandler;
import com.lesserhydra.util.StringTools;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IsVariable implements Condition.ForPlayer {
	//(\w+)\s*([=<>]+)\s*(\w+)
	private static final Pattern isVariableLinePattern = Pattern.compile("(\\w+)\\s*([=<>]+)\\s*(\\w+)");
	
	private final String 				name;
	private final VariableConditional	conditional;
	private final String				otherName;
	private final int					number;
	
	IsVariable(String variableLine) {
		//Match
		Matcher lineMatcher = isVariableLinePattern.matcher(variableLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid variable condition line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + variableLine, LogType.CONFIG_ERRORS);
			name = "";
			conditional = VariableConditional.EQUAL;
			number = 0;
			otherName = null;
			return;
		}
		
		//Get variable name
		name = lineMatcher.group(1);
		
		//Get operator
		conditional = VariableConditional.fromSymbol(lineMatcher.group(2));
		
		//Operand may be an integer or the name of a variable
		String operand = lineMatcher.group(3);
		if (StringTools.isInteger(operand)) {
			number = Integer.parseInt(operand);
			otherName = null;
		}
		else {
			number = 0;
			otherName = operand;
		}
	}

	@Override
	public boolean test(Player target) {
		int a = VariableHandler.get(target, name);
		int b = (otherName != null ? VariableHandler.get(target, otherName) : number);
		return conditional.check(a, b);
	}

}
