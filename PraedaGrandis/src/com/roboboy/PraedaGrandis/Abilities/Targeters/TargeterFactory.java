package com.roboboy.PraedaGrandis.Abilities.Targeters;

public class TargeterFactory
{
	public static final String PREFIX = "@";
	
	public static Targeter getDefault() {
		return new DefaultTargeter();
	}
	
	public static Targeter build(String targeterString)
	{
		//Remove prefix
		targeterString = targeterString.replace(PREFIX, "");
		
		//Get argument, if exists
		int argIndex = targeterString.indexOf('(');
		String argument;
		if (argIndex != -1) {
			argument = targeterString.substring(argIndex + 1, targeterString.length() - 1);
			targeterString = targeterString.substring(0, argIndex);
		}
		else {
			argument = "";
		}
		
		//Build the Targeter
		Targeter t = constructTargeter(targeterString, argument);
		
		return t;
	}
	
	private static Targeter constructTargeter(String name, String argument)
	{
		switch (name) {
		case "holder":		return new HolderTargeter();
		case "activator":	return new ActivatorTargeter();
		case "inradius":	return new InRadiusTargeter(Double.parseDouble(argument));
		case "mount":		return new MountTargeter();
		case "rider":		return new RiderTargeter();
		case "default":		return new DefaultTargeter();
		
		default:			return null;
		}
	}

}
