package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.LogType;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Tools;
import com.roboboy.PraedaGrandis.VariableHandler;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;
import com.roboboy.PraedaGrandis.Configuration.VariableOperator;

public class VariableAbility extends Ability
{	
	final private String 			name;
	final private VariableOperator	operator;
	final private String			otherName;
	final private int				number;
	
	public VariableAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, ConfigString args)
	{
		super(slotType, activator, targeter);
		if (args.size() > 4) {
			name = args.get(1);
			operator = VariableOperator.fromSymbol(args.get(2));
			
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
			operator = null;
			otherName = null;
			number = 0;
		}
	}

	@Override
	protected void execute(Target target)
	{
		if (!(target.get() instanceof Player)) return;
		
		Player p = (Player) target.get();
		VariableHandler.operate(p, name, operator, (otherName != null ? VariableHandler.get(p, otherName) : number));
	}

}
