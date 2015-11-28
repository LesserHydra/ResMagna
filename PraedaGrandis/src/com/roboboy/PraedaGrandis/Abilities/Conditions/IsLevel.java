package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;
import com.roboboy.PraedaGrandis.Configuration.VariableConditional;

public class IsLevel extends Condition
{
	final private VariableConditional	conditional;
	final private int					number;

	public IsLevel(Targeter targeter, boolean not, ConfigString args)
	{
		super(targeter, not);
		
		conditional = VariableConditional.fromSymbol(args.get(1));
		number = Integer.parseInt(args.get(2));
	}

	@Override
	protected boolean checkThis(Target target)
	{
		if (!(target.get() instanceof Player)) return false;
		Player p = (Player) target.get();
		return conditional.check(p.getLevel(), number);
	}

}
