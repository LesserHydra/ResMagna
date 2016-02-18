package com.roboboy.PraedaGrandis.Abilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Tools;
import com.roboboy.PraedaGrandis.VariableHandler;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.VariableOperator;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;

class VariableAbility extends Ability
{
	//(\w+)\s*([=+\-*/%]+)\s*(\w+)
	static private final Pattern variableLinePattern = Pattern.compile("(\\w+)\\s*([=+\\-*/%]+)\\s*(\\w+)");
	
	private final String 			name;
	private final VariableOperator	operator;
	private final String			otherName;
	private final int				number;
	
	public VariableAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, String variableLine)
	{
		super(slotType, activator, targeter);
		
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
	protected void execute(Target target)
	{
		if (!(target.getEntity() instanceof Player)) return;
		
		Player p = (Player) target.getEntity();
		VariableHandler.operate(p, name, operator, (otherName != null ? VariableHandler.get(p, otherName) : number));
	}

}
