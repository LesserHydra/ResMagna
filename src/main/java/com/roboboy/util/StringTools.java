package com.roboboy.util;

import java.util.Set;
import java.util.regex.Pattern;
import com.google.common.collect.ImmutableSet;

public class StringTools
{
	private static Set<String> TRUE_VALUES = ImmutableSet.of("true", "t", "yes", "y", "1");
	private static Set<String> FALSE_VALUES = ImmutableSet.of("false", "f", "no", "n", "0");
	private static Pattern integerPattern = Pattern.compile("[+-]?\\d+");
	private static Pattern floatPattern = Pattern.compile("[+-]?((\\d+(\\.\\d*)?)|(\\.\\d+))");
	
	/**
	 * Checks if a given string represents a boolean.
	 * @param str - Non-null string to check
	 * @return True if string represents a boolean, false otherwise.
	 */
	public static boolean isBoolean(String str) {
		return (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("false"));
	}
	
	/**
	 * Checks if a given string represents an extended boolean.<br>
	 * Supported case-insensitive values are:<br>
	 * - True: True, T, Yes, Y, 1<br>
	 * - False: False, F, No, N, 0<br>
	 * @param str - Non-null string to check
	 * @return True if string represents a boolean, false otherwise.
	 */
	public static boolean isExtendedBoolean(String str) {
		str = str.toLowerCase();
		return TRUE_VALUES.contains(str) || FALSE_VALUES.contains(str);
	}
	
	/**
	 * Checks if a given string represents an integer.<br>
	 * <br>
	 * Note that it does <b>NOT</b> check for overflow.
	 * @param str - Non-null string to check
	 * @return True if string represents an integer, false otherwise.
	 */
	public static boolean isInteger(String str) {
		return integerPattern.matcher(str).matches();
	}
	
	/**
	 * Checks if a given string represents a floating point number.<br>
	 * <br>
	 * Note that it does <b>NOT</b> check for overflow.
	 * @param str - Non-null string to check
	 * @return True if string represents a float, false otherwise.
	 */
	public static boolean isFloat(String str) {
		return floatPattern.matcher(str).matches();
	}
	
	/**
	 * Parses the string as an extended boolean, as described in {@link #isExtendedBoolean(String)}<br>
	 * <br>
	 * The method only checks for true values; any unknown string will be treated as false.
	 * @param str Non-null string to parse
	 * @return True if the string represents a true value, false otherwise
	 */
	public static boolean parseExtendedBoolean(String str) {
		return (TRUE_VALUES.contains(str.toLowerCase()));
	}

}
