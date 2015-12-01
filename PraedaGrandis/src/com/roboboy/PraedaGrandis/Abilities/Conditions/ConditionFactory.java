package com.roboboy.PraedaGrandis.Abilities.Conditions;

import com.roboboy.PraedaGrandis.LogType;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;

public class ConditionFactory
{	
	public static Condition build(String s)
	{
		ConfigString args = new ConfigString(s);
		
		//Cannot be empty
		if (args.size() == 0) return null;
		
		//Get name and NOT opperator
		String cName = args.get(0);
		boolean not = false;
		if (cName.startsWith("~")) {
			not = true;
			cName = cName.substring(1);
		}
		
		//Targeter
		Targeter targeter;
		int targeterIndex = args.contains(TargeterFactory.PREFIX);
		if (targeterIndex != -1) {
			targeter = TargeterFactory.build(args.get(targeterIndex));
		}
		else {
			targeter = TargeterFactory.getDefault();
		}
		
		//Choose proper condition by name
		Condition c = createCondition(cName, targeter, not, args);
		
		if (c != null) {
			PraedaGrandis.log(s, LogType.CONFIG_PARSING);
			PraedaGrandis.log("\tConditionType: " + cName, LogType.CONFIG_PARSING);
			PraedaGrandis.log("\tInversed: " + not, LogType.CONFIG_PARSING);
			PraedaGrandis.log("\tTargeterType: " + targeter.getClass().getSimpleName(), LogType.CONFIG_PARSING);
		}
		
		return c;
	}
	
	private static Condition createCondition(String name, Targeter targeter, boolean not, ConfigString args)
	{
		switch (name) {
		case "isnull":			return new IsNull(targeter, not);
		case "isholder":		return new IsHolder(targeter, not);
		case "isplayer":		return new IsPlayer(targeter, not);
		case "ismob":			return new IsMob(targeter, not);
		case "isblock":			return new IsBlock(targeter, not, args);
		case "issneaking":		return new IsSneaking(targeter, not);
		case "issprinting":		return new IsSprinting(targeter, not);
		case "isonground":		return new IsOnGround(targeter, not);
		case "issheltered":		return new IsSheltered(targeter, not);
		case "isblocking":		return new IsBlocking(targeter, not);
		case "issleeping":		return new IsSleeping(targeter, not);
		case "iswearing":		return new IsWearing(targeter, not, args);
		case "isholding":		return new IsHolding(targeter, not, args);
		case "isvariable":		return new IsVariable(targeter, not, args);
		case "ishealth":		return new IsHealth(targeter, not, args);
		case "ishunger":		return new IsHunger(targeter, not, args);
		case "isexp":			return new IsExp(targeter, not, args);
		case "islevel":			return new IsLevel(targeter, not, args);
		case "israining":		return new IsRaining(targeter, not);
		case "isthundering":	return new IsThundering(targeter, not);
		case "ismount":			return new IsMount(targeter, not);
		case "isriding":		return new IsRiding(targeter, not);
		
		default:				return null;
		}
	}
}
