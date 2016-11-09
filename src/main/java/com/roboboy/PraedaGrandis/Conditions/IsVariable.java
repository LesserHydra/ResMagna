package com.roboboy.PraedaGrandis.Conditions;

import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.VariableConditional;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;
import com.roboboy.PraedaGrandis.VariableHandler;
import com.roboboy.util.StringTools;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IsVariable implements Condition {
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
	public boolean test(Target target) {
		if (!(target.getEntity() instanceof Player)) return false;
		Player p = (Player) target.getEntity();
		
		int a = VariableHandler.get(p, name);
		int b = (otherName != null ? VariableHandler.get(p, otherName) : number);
		
		return conditional.check(a, b);
	}

}
