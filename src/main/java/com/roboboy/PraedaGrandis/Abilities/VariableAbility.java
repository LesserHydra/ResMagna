package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Function.Functor;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Arguments.VariableOperator;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;
import com.roboboy.PraedaGrandis.VariableHandler;
import com.roboboy.util.StringTools;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class VariableAbility implements Functor {
	
	//(\w+)\s*([=+\-*/%]+)\s*(\w+)
	static private final Pattern variableLinePattern = Pattern.compile("(\\w+)\\s*([=+\\-*/%]+)\\s*(\\w+)");
	
	private final String 			name;
	private final VariableOperator	operator;
	private final String			otherName;
	private final int				number;
	
	VariableAbility(String variableLine) {
		//Match
		Matcher lineMatcher = variableLinePattern.matcher(variableLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid variable line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + variableLine, LogType.CONFIG_ERRORS);
			name = "";
			operator = VariableOperator.SET;
			number = 0;
			otherName = null;
			return;
		}
		
		//Get variable name
		name = lineMatcher.group(1);
		
		//Get operator
		operator = VariableOperator.fromSymbol(lineMatcher.group(2));
		
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
	public void run(Target target) {
		if (!target.isPlayer()) return;
		Player p = target.asPlayer();
		VariableHandler.operate(p, name, operator, (otherName != null ? VariableHandler.get(p, otherName) : number));
	}

}
