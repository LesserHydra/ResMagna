package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.VariableHandler;
import com.lesserhydra.resmagna.arguments.VariableConditional;
import com.lesserhydra.resmagna.arguments.VariableConstruct;
import com.lesserhydra.resmagna.arguments.VariableConstructs;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.variables.Variable;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IsVariable implements Condition.ForPlayer {
	//(\w+)\s*([=<>]+)\s*(\w+)
	private static final Pattern isVariableLinePattern = Pattern.compile("(\\w+)\\s*([=<>]+)\\s*(\\w+)");
	
	private final String 				name;
	private final VariableConditional	conditional;
	private final VariableConstruct     other;
	
	IsVariable(String variableLine) {
		//Match
		Matcher lineMatcher = isVariableLinePattern.matcher(variableLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid variable condition line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + variableLine, LogType.CONFIG_ERRORS);
			name = "";
			conditional = VariableConditional.EQUAL;
			other = VariableConstructs.NONE;
			return;
		}
		
		//Get variable name
		name = lineMatcher.group(1);
		
		//Get operator
		conditional = VariableConditional.fromSymbol(lineMatcher.group(2));
		
		//Operand may be an integer or the name of a variable
		String operand = lineMatcher.group(3);
		other = VariableConstructs.construct(operand);
	}

	@Override
	public boolean test(Player target) {
		Variable a = VariableHandler.get(target, name);
		Variable b = other.get(target);
		
		if (!a.hasNumber() || !b.hasNumber()) {
			GrandLogger.log("Tried to compare non-numerical values.", LogType.RUNTIME_ERRORS);
			return false;
		}
		return conditional.check(a.getDouble(), b.getDouble()); //TODO
		
	}

}
