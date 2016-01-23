package com.roboboy.PraedaGrandis.Configuration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Location;

public class GrandLocation
{
	private final static Pattern locComponentPattern = Pattern.compile("(~?([+-]?((\\d+(\\.\\d*)?)|(\\.\\d+)))|~([+-]?((\\d+(\\.\\d*)?)|(\\.\\d+)))?)");
	
	private final double x;
	private final double y;
	private final double z;
	
	private final boolean relX;
	private final boolean relY;
	private final boolean relZ;
	
	public GrandLocation(double x, double y, double z, boolean relX, boolean relY, boolean relZ) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.relX = relX;
		this.relY = relY;
		this.relZ = relZ;
	}
	
	/**
	 * Constructs a GrandLocation from the given string. The string must have already been checked for validity.<br>
	 * <pre>Format is as follows:
	 * Enclosed in brackets ("()")
	 * Three components:
	 *  Either a tilda ("~"), denoting relativity, or a floating value
	 *  Float may be prefixed with a tilda, denoting relativity</pre>
	 * @param locString String to construct from
	 */
	public GrandLocation(String locString) {
		Matcher match = locComponentPattern.matcher(locString);
		
		//X component
		match.find();
		relX = (match.group(0).charAt(0) == '~');
		x = getComponentValue(match.group(2));
		
		//Y component
		match.find();
		relY = (match.group(0).charAt(0) == '~');
		y = getComponentValue(match.group(2));
		
		//Z component
		match.find();
		relZ = (match.group(0).charAt(0) == '~');
		z = getComponentValue(match.group(2));
	}
	
	public Location calculate(Location relativeLocation) {
		Location finalLoc = relativeLocation.clone();
		finalLoc.add(x, y, z);
		if (!relX) finalLoc.setX(x);
		if (!relY) finalLoc.setY(y);
		if (!relZ) finalLoc.setZ(z);
		
		return finalLoc;
	}
	
	private double getComponentValue(String valueString) {
		if (valueString == null) return 0;
		return Double.parseDouble(valueString);
	}
}
