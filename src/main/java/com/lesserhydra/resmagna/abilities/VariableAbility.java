package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.variables.ConstructFactory;
import com.lesserhydra.resmagna.variables.ValueConstruct;
import com.lesserhydra.resmagna.variables.Constructs;
import com.lesserhydra.resmagna.variables.ValueOperator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class VariableAbility implements Ability {
	
	//([\w.]+)\s*([=+\-*/%]+)\s*([\w.]+)
	static private final Pattern variableLinePattern = Pattern.compile("([\\w.]+)\\s*([=+\\-*/%]+)\\s*([\\w.]+)");
	
	private final ValueConstruct var;
	private final ValueOperator operator;
	private final ValueConstruct other;
	
	VariableAbility(String variableLine) {
		//Match
		Matcher lineMatcher = variableLinePattern.matcher(variableLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid variable line format.", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + variableLine, LogType.CONFIG_ERRORS);
			var = Constructs.NONE;
			operator = ValueOperator.SET;
			other = Constructs.NONE;
			return;
		}
		
		//Get variable name
		String lhsString = lineMatcher.group(1);
		ValueConstruct workingVar = ConstructFactory.parse(lhsString);
		if (!workingVar.isSettable()) {
			GrandLogger.log("Left hand operand is not settable: " + lhsString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + variableLine, LogType.CONFIG_ERRORS);
			var = Constructs.NONE;
			operator = ValueOperator.SET;
			other = Constructs.NONE;
			return;
		}
		var = workingVar;
		
		//Get operator
		operator = ValueOperator.fromSymbol(lineMatcher.group(2));
		
		//Operand may be an integer or the name of a variable
		String operand = lineMatcher.group(3);
		other = ConstructFactory.parse(operand);
	}

	@Override
	public void run(Target target) {
		//TODO: Maybe this should be handled by the operators themselves?
		var.set(target, operator.apply(var.evaluate(target), other.evaluate(target)));
	}

}
