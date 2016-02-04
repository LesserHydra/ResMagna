package com.roboboy.PraedaGrandis.Abilities.Conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Tools;
import com.roboboy.PraedaGrandis.VariableHandler;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.VariableConditional;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;

class IsVariable extends Condition
{
	//(\w+)\s*([=<>]+)\s*(\w+)
	private static final Pattern isVariableLinePattern = Pattern.compile("(\\w+)\\s*([=<>]+)\\s*(\\w+)");
	
	private final String 				name;
	private final VariableConditional	conditional;
	private final String				otherName;
	private final int					number;
	
	public IsVariable(Targeter targeter, boolean not, String variableLine)
	{
		super(targeter, not);
		
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
		if (Tools.isInteger(operand)) {
			number = Integer.parseInt(operand);
			otherName = null;
		}
		else {
			number = 0;
			otherName = operand;
		}
	}

	@Override
	protected boolean checkThis(Target target)
	{
		if (!(target.get() instanceof Player)) return false;
		Player p = (Player) target.get();
		
		int a = VariableHandler.get(p, name);
		int b = (otherName != null ? VariableHandler.get(p, otherName) : number);
		
		return conditional.check(a, b);
	}

}
