package com.roboboy.PraedaGrandis.Configuration;

import org.bukkit.Location;

public class GrandLocation
{
	private final double x;
	private final double y;
	private final double z;
	
	private final boolean relX;
	private final boolean relY;
	private final boolean relZ;
	
	public GrandLocation(double x, double y, double z, boolean relX, boolean relY, boolean relZ)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.relX = relX;
		this.relY = relY;
		this.relZ = relZ;
	}
	
	public GrandLocation(String locString)
	{
		//TODO: Error handling. What if bad string was given?
		String[] args = locString.replace("(", "").replace(")", "").split(" ");
		
		relX = (args[0].charAt(0) == '~');
		relY = (args[1].charAt(0) == '~');
		relZ = (args[2].charAt(0) == '~');
		
		x = Double.parseDouble(args[0].replace("~", "0").replace("0-", "-"));
		y = Double.parseDouble(args[1].replace("~", "0").replace("0-", "-"));
		z = Double.parseDouble(args[2].replace("~", "0").replace("0-", "-"));
	}

	public Location calculate(Location relativeLocation)
	{
		Location finalLoc = relativeLocation.clone();
		finalLoc.add(x, y, z);
		if (!relX) finalLoc.setX(x);
		if (!relY) finalLoc.setY(y);
		if (!relZ) finalLoc.setZ(z);
		
		return finalLoc;
	}
}
