package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.VariableConstruct;
import com.lesserhydra.resmagna.arguments.VariableConstructs;
import com.lesserhydra.resmagna.function.Functor;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.arguments.VariableOperator;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.VariableHandler;
import com.lesserhydra.util.StringTools;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class VariableAbility implements Ability.ForPlayer {
	
	//(\w+)\s*([=+\-*/%]+)\s*(\w+)
	static private final Pattern variableLinePattern = Pattern.compile("(\\w+)\\s*([=+\\-*/%]+)\\s*(\\w+)");
	
	private final String 			name;
	private final VariableOperator	operator;
	private final VariableConstruct other;
	
	VariableAbility(String variableLine) {
		//Match
		Matcher lineMatcher = variableLinePattern.matcher(variableLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid variable line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + variableLine, LogType.CONFIG_ERRORS);
			name = "";
			operator = VariableOperator.SET;
			other = VariableConstructs.NONE;
			return;
		}
		
		//Get variable name
		name = lineMatcher.group(1);
		
		//Get operator
		operator = VariableOperator.fromSymbol(lineMatcher.group(2));
		
		//Operand may be an integer or the name of a variable
		String operand = lineMatcher.group(3);
		other = VariableConstructs.construct(operand);
	}

	@Override
	public void run(Player target) {
		VariableHandler.operate(target, name, operator, other);
	}

}
