package com.roboboy.PraedaGrandis.Abilities.Targeters;

import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class TargeterFactory
{
	public static Targeter build(String targeterName, String targeterArgument) {
		Targeter t = constructTargeter(targeterName, targeterArgument);
		if (t == null) {
			t = new DefaultTargeter();
			PraedaGrandis.plugin.logger.log("Invalid targeter: " + targeterName, LogType.CONFIG_ERRORS);
		}
		return t;
	}
	
	private static Targeter constructTargeter(String name, String argument)
	{
		switch (name) {
		case "holder":		return new HolderTargeter();
		case "activator":	return new ActivatorTargeter();
		case "inradius":	return new InRadiusTargeter(Double.parseDouble(argument)); //TODO: Error logging/handling
		case "mount":		return new MountTargeter();
		case "rider":		return new RiderTargeter();
		case "default":		return new DefaultTargeter();
		
		default:			return null;
		}
	}

}
