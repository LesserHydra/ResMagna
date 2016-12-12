package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.variables.Variable;
import com.lesserhydra.resmagna.variables.VariableConditional;
import com.lesserhydra.resmagna.variables.VariableConstruct;
import com.lesserhydra.resmagna.variables.VariableConstructs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IsVariable implements Condition {
	
	//([\w.]+)\s*([=<>]+)\s*([\w.]+)
	private static final Pattern isVariableLinePattern = Pattern.compile("([\\w.]+)\\s*([=<>]+)\\s*([\\w.]+)");
	
	private final VariableConstruct     var;
	private final VariableConditional	conditional;
	private final VariableConstruct     other;
	
	IsVariable(String variableLine) {
		//Match
		Matcher lineMatcher = isVariableLinePattern.matcher(variableLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid variable condition line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + variableLine, LogType.CONFIG_ERRORS);
			var = VariableConstructs.NONE;
			conditional = VariableConditional.EQUAL;
			other = VariableConstructs.NONE;
			return;
		}
		
		//Get variable name
		String lhsString = lineMatcher.group(1);
		var = VariableConstructs.parse(lhsString);
		
		//Get operator
		conditional = VariableConditional.fromSymbol(lineMatcher.group(2));
		
		//Operand may be an integer or the name of a variable
		String operand = lineMatcher.group(3);
		other = VariableConstructs.parse(operand);
	}
	
	public IsVariable(VariableConstruct var, VariableConditional conditional, VariableConstruct other) {
		this.var = var;
		this.conditional = conditional;
		this.other = other;
	}
	
	@Override
	public boolean test(Target target) {
		Variable a = var.get(target);
		Variable b = other.get(target);
		
		if (!a.hasNumber() || !b.hasNumber()) {
			GrandLogger.log("Tried to compare non-numerical values.", LogType.RUNTIME_ERRORS);
			return false;
		}
		return conditional.check(a.getDouble(), b.getDouble()); //TODO: Proper specialization
	}

}
