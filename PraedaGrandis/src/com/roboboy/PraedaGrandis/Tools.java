package com.roboboy.PraedaGrandis;

import java.util.EnumSet;
import java.util.regex.Pattern;

public class Tools
{
	private static Pattern integerPattern = Pattern.compile("[+-]?\\d+");
	private static Pattern floatPattern = Pattern.compile("[+-]?((\\d+(\\.\\d*)?)|(\\.\\d+))");
	private static Pattern enumSeperaterPattern = Pattern.compile("[_\\s]");
	
	/**
	 * Checks if a given string represents a boolean.
	 * @param str - String to check
	 * @return True if string represents a boolean, false otherwise.
	 */
	public static boolean isBoolean(String str) {
		if (str == null) return false;
		return (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("false"));
	}
	
	/**
	 * Checks if a given string represents an integer. Note that it does
	 * <b>NOT</b> check for overflow.
	 * @param str - String to check
	 * @return True if string represents an integer, false otherwise.
	 */
	public static boolean isInteger(String str) {
		return integerPattern.matcher(str).matches();
	}
	
	/**
	 * Checks if a given string represents a floating point number. Note
	 * that it does <b>NOT</b> check for overflow.
	 * @param str - String to check
	 * @return True if string represents a float, false otherwise.
	 */
	public static boolean isFloat(String str) {
		return floatPattern.matcher(str).matches();
	}

	public static <T extends Enum<T>> T parseEnum(String lookupName, Class<T> enumClass) {
		//Find enum from value
		lookupName = enumSeperaterPattern.matcher(lookupName.toUpperCase()).replaceAll("");
		for (T type : EnumSet.allOf(enumClass)) {
			String enumName = enumSeperaterPattern.matcher(type.name()).replaceAll("");
			String enumString = enumSeperaterPattern.matcher(type.toString().toUpperCase()).replaceAll("");
			if (lookupName.equals(enumName) || lookupName.equals(enumString)) return type;
		}
		//Not found
		return null;
	}
}
