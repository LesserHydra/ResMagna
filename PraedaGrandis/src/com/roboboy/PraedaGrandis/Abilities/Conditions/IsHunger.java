package com.roboboy.PraedaGrandis.Abilities.Conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Tools;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.VariableConditional;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class IsHunger extends Condition
{
	//([=<>]+)\s*(\w+)
	static private final Pattern isLinePattern = Pattern.compile("([=<>]+)\\s*(\\w+)");
	
	final private VariableConditional	conditional;
	final private float					number;

	public IsHunger(Targeter targeter, boolean not, String argLine)
	{
		super(targeter, not);
		
		//Match
		Matcher lineMatcher = isLinePattern.matcher(argLine);
		if (!lineMatcher.matches()) {
			PraedaGrandis.plugin.logger.log("Invalid hunger condition line format:", LogType.CONFIG_ERRORS);
			PraedaGrandis.plugin.logger.log("  " + argLine, LogType.CONFIG_ERRORS);
			conditional = VariableConditional.EQUAL;
			number = 0;
			return;
		}
		
		//Get operator
		conditional = VariableConditional.fromSymbol(lineMatcher.group(1));
		
		//Operand may be an integer or the name of a variable
		String operand = lineMatcher.group(2);
		if (!Tools.isFloat(operand)) {
			PraedaGrandis.plugin.logger.log("Invalid hunger condition operand:", LogType.CONFIG_ERRORS);
			PraedaGrandis.plugin.logger.log("  " + argLine, LogType.CONFIG_ERRORS);
			number = 0;
			return;
		}
		number = Float.parseFloat(operand);
	}

	@Override
	protected boolean checkThis(Target target)
	{
		if (!(target.get() instanceof Player)) return false;
		Player p = (Player) target.get();
		return conditional.check(p.getSaturation(), number);
	}

}
