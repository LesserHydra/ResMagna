package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.variables.Value;
import com.lesserhydra.resmagna.variables.ValueConditional;
import com.lesserhydra.resmagna.variables.ValueConstruct;
import com.lesserhydra.resmagna.variables.ValueConstructs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IsVariable implements Condition {
	
	//([\w.]+)\s*([=<>]+)\s*([+\-]?[\w.]+)
	private static final Pattern isVariableLinePattern = Pattern.compile("([\\w.]+)\\s*([=<>]+)\\s*([+\\-]?[\\w.]+)");
	
	private final ValueConstruct var;
	private final ValueConditional conditional;
	private final ValueConstruct other;
	
	IsVariable(String variableLine) {
		//Match
		Matcher lineMatcher = isVariableLinePattern.matcher(variableLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid variable condition line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + variableLine, LogType.CONFIG_ERRORS);
			var = ValueConstructs.NONE;
			conditional = ValueConditional.EQUAL;
			other = ValueConstructs.NONE;
			return;
		}
		
		//Get variable name
		String lhsString = lineMatcher.group(1);
		var = ValueConstructs.parse(lhsString);
		
		//Get operator
		conditional = ValueConditional.fromSymbol(lineMatcher.group(2));
		
		//Operand may be an integer or the name of a variable
		String operand = lineMatcher.group(3);
		other = ValueConstructs.parse(operand);
	}
	
	public IsVariable(ValueConstruct var, ValueConditional conditional, ValueConstruct other) {
		this.var = var;
		this.conditional = conditional;
		this.other = other;
	}
	
	@Override
	public boolean test(Target target) {
		Value a = var.get(target);
		Value b = other.get(target);
		
		if (!a.hasNumber() || !b.hasNumber()) {
			GrandLogger.log("Tried to compare non-numerical values.", LogType.RUNTIME_ERRORS);
			return false;
		}
		return conditional.check(a.asDouble(), b.asDouble()); //TODO: Proper specialization
	}

}
