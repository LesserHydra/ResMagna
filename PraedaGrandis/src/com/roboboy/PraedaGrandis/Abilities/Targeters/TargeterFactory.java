package com.roboboy.PraedaGrandis.Abilities.Targeters;

public class TargeterFactory
{
	public static final String PREFIX = "@";
	
	public static Targeter build(String targeterString)
	{
		//Remove prefix
		targeterString = targeterString.replace(PREFIX, "");
		
		//Get argument, if exists
		int argIndex = targeterString.indexOf('(');
		String argument;
		if (argIndex != -1) {
			argument = targeterString.substring(argIndex).replace("(", "").replace(")", "");
			targeterString = targeterString.substring(0, argIndex);
		}
		else {
			argument = "";
		}
		
		//Build the Targeter
		Targeter t = null;
		switch (targeterString)
		{
		case "holder": t = new HolderTargeter();
			break;
		case "activator": t = new ActivatorTargeter();
			break;
		case "inradius": t = new InRadiusTargeter(Double.parseDouble(argument));
			break;
		case "default": t = new DefaultTargeter();
			break;
		}
		
		return t;
	}

	public static Targeter getDefault() {
		return new DefaultTargeter();
	}

}
