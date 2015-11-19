package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.LogType;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Tools;
import com.roboboy.PraedaGrandis.VariableHandler;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;
import com.roboboy.PraedaGrandis.Configuration.VariableConditional;

public class IsVariable extends Condition
{
	final private String 				name;
	final private VariableConditional	conditional;
	final private String			otherName;
	final private int				number;
	
	public IsVariable(Targeter targeter, boolean not, ConfigString args)
	{
		super(targeter, not);
		if (args.size() > 4) {
			name = args.get(1);
			conditional = VariableConditional.fromSymbol(args.get(2));
			
			//Third argument can be an integer or the name of a variable
			if (Tools.isInteger(args.get(3))) {
				number = Integer.parseInt(args.get(3));
				otherName = null;
			}
			else {
				number = 0;
				otherName = args.get(3);
			}
		}
		else {
			//Error
			PraedaGrandis.log("Not enough arguments in variable ability line:", LogType.CONFIG_ERRORS);
			PraedaGrandis.log("  " + args.getOriginalString(), LogType.CONFIG_ERRORS);
			PraedaGrandis.log("  Has " + args.size() + ", requires at least 4.", LogType.CONFIG_ERRORS);
			name = null;
			conditional = null;
			otherName = null;
			number = 0;
		}
	}

	@Override
	protected boolean checkThis(LivingEntity target)
	{
		if (!(target instanceof Player)) return false;
		Player p = (Player) target;
		return VariableHandler.checkCondition(p, name, conditional, (otherName != null ? VariableHandler.get(p, otherName) : number));
	}

}
