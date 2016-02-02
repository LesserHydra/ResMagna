package com.roboboy.PraedaGrandis;

import java.util.regex.Pattern;

public class Tools
{
	private static Pattern integerPattern = Pattern.compile("[+-]?\\d+");
	private static Pattern floatPattern = Pattern.compile("[+-]?((\\d+(\\.\\d*)?)|(\\.\\d+))");
	
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
}
