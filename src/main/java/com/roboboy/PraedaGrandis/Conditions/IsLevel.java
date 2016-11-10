package com.roboboy.PraedaGrandis.Conditions;

import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Arguments.VariableConditional;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;
import com.roboboy.util.StringTools;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IsLevel implements Condition {
	
	//([=<>]+)\s*(\w+)
	private static final Pattern isLinePattern = Pattern.compile("([=<>]+)\\s*(\\w+)");
	
	private final VariableConditional	conditional;
	private final int					number;

	IsLevel(String argLine) {
		//Match
		Matcher lineMatcher = isLinePattern.matcher(argLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid level condition line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + argLine, LogType.CONFIG_ERRORS);
			conditional = VariableConditional.EQUAL;
			number = 0;
			return;
		}
		
		//Get operator
		conditional = VariableConditional.fromSymbol(lineMatcher.group(1));
		
		//Operand may be an integer or the name of a variable
		String operand = lineMatcher.group(2);
		if (!StringTools.isFloat(operand)) {
			GrandLogger.log("Invalid level condition operand:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + argLine, LogType.CONFIG_ERRORS);
			number = 0;
			return;
		}
		number = Integer.parseInt(operand);
	}

	@Override
	public boolean test(Target target) {
		if (!target.isPlayer()) return false;
		Player p = target.asPlayer();
		return conditional.check(p.getLevel(), number);
	}

}
