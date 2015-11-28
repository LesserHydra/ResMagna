package com.roboboy.PraedaGrandis.Abilities.Conditions;

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
		Condition c = null;
		switch (cName)
		{
		case "isnull": c = new IsNull(targeter, not);
		break;
		case "isholder": c = new IsHolder(targeter, not);
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
		case "isonground": c = new IsOnGround(targeter, not);
		break;
		case "isblocking": c = new IsBlocking(targeter, not);
		break;
		case "issleeping": c = new IsSleeping(targeter, not);
		break;
		case "iswearing": c = new IsWearing(targeter, not, args);
		break;
		case "isvariable": c = new IsVariable(targeter, not, args);
		break;
		case "ishealth": c = new IsHealth(targeter, not, args);
		break;
		case "ishunger": c = new IsHunger(targeter, not, args);
		break;
		case "isexp": c = new IsExp(targeter, not, args);
		break;
		case "islevel": c = new IsLevel(targeter, not, args);
		break;
		case "israining": c = new IsRaining(targeter, not);
		break;
		case "isthundering": c = new IsThundering(targeter, not);
		break;
		}
		
		if (c != null) {
			PraedaGrandis.plugin.getLogger().info(s);
			PraedaGrandis.plugin.getLogger().info("\tConditionType: " + cName);
			PraedaGrandis.plugin.getLogger().info("\tInversed: " + not);
			PraedaGrandis.plugin.getLogger().info("\tTargeterType: " + targeter.getClass().getSimpleName());
			
			/*if (args.size() != 0) {
				PraedaGrandis.plugin.getLogger().info("\tArguments:");
				for (String argString : args) {
					PraedaGrandis.plugin.getLogger().info("\t- " + argString);
				}
			}*/
		}
		
		return c;
	}
}
