package com.roboboy.PraedaGrandis.Abilities.Conditions;

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
		if (cName.startsWith("!")) {
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
		Condition c = null;
		switch (cName)
		{
		case "isnull": c = new IsNull(targeter, not);
		break;
		case "isplayer": c = new IsPlayer(targeter, not);
		break;
		case "ismob": c = new IsMob(targeter, not);
		break;
		case "isblock": c = new IsBlock(targeter, not, args);
		break;
		case "issneaking": c = new IsSneaking(targeter, not);
		break;
		case "issprinting": c = new IsSprinting(targeter, not);
		break;
		case "isblocking": c = new IsBlocking(targeter, not);
		break;
		case "iswearing": c = new IsWearing(targeter, not, args);
		break;
		case "isvariable": c = new IsVariable(targeter, not, args);
		break;
		}
		
		return c;
	}
}
