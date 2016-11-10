package com.roboboy.PraedaGrandis.Arguments;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roboboy.PraedaGrandis.Configuration.FunctionRunner;
import com.roboboy.PraedaGrandis.Configuration.GroupingParser;
import org.bukkit.Color;
import com.roboboy.PraedaGrandis.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;
import com.roboboy.util.StringTools;

public class ArgumentBlock {
	
	//(\w+)\s*=\s*([^,;\s\n\(]*(?:\s*\((\$[\d]+)\))?)
	static private final Pattern argumentPattern = Pattern.compile("(\\w+)\\s*=\\s*([^,;\\s\\n\\(]*(?:\\s*\\((\\$[\\d]+)\\))?)");
	
	private final String lineString;
	private final Map<String, String> argumentMap = new HashMap<>();
	
	public ArgumentBlock(String argumentString, String lineString) {
		this.lineString = lineString;
		
		if (argumentString == null) return;
		
		GroupingParser groupParser = new GroupingParser(argumentString);
		String simplifiedArgumentString = groupParser.getSimplifiedString();
		
		Matcher argumentMatcher = argumentPattern.matcher(simplifiedArgumentString);
		while (argumentMatcher.find()) {
			String argumentName = argumentMatcher.group(1).toLowerCase();
			String argumentValue = argumentMatcher.group(2);
			String argumentValueGroupID = argumentMatcher.group(3);
			argumentValue = groupParser.readdGrouping(argumentValue, argumentValueGroupID);
			argumentMap.put(argumentName, argumentValue);
		}
		
		if (argumentMap.isEmpty()) argumentMap.put(null, argumentString);
	}
	
	/**
	 * Gets the string associated with the given key.<br>
	 * Logs an error if required and none found.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Raw string value of argument, or fallback if none exists
	 */
	public String getString(boolean required, String fallback, String... keys) {
		String result = findValue(required, keys);
		if (result == null) return fallback;
		return result;
	}
	
	/**
	 * Gets the boolean associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Boolean value of argument, or fallback if none exists
	 */
	public boolean getBoolean(boolean required, boolean fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		if (!StringTools.isExtendedBoolean(value)) {
			logInvalid(keys, value, "boolean");
			return fallback;
		}
		
		return StringTools.parseExtendedBoolean(value);
	}
	
	/**
	 * Gets the integer associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Integer value of argument, or fallback if none exists
	 */
	public int getInteger(boolean required, int fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		if (!StringTools.isInteger(value)) {
			logInvalid(keys, value, "integer");
			return fallback;
		}
		
		return Integer.parseInt(value);
	}
	
	/**
	 * Gets the long associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Long value of argument, or fallback if none exists
	 */
	public long getLong(boolean required, long fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		if (!StringTools.isInteger(value)) {
			logInvalid(keys, value, "long");
			return fallback;
		}
		
		return Long.parseLong(value);
	}
	
	/**
	 * Gets the float associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Float value of argument, or fallback if none exists
	 */
	public float getFloat(boolean required, float fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		if (!StringTools.isFloat(value)) {
			logInvalid(keys, value, "float");
			return fallback;
		}
		
		return Float.parseFloat(value);
	}
	
	/**
	 * Gets the double associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Double value of argument, or fallback if none exists
	 */
	public double getDouble(boolean required, double fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		if (!StringTools.isFloat(value)) {
			logInvalid(keys, value, "double");
			return fallback;
		}
		
		return Double.parseDouble(value);
	}
	
	/**
	 * Gets the GrandLocation associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return GrandLocation value of argument, or fallback if none exists
	 */
	public GrandLocation getLocation(boolean required, GrandLocation fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		GrandLocation result = GrandLocation.buildFromString(value.substring(1, value.length()-1));
		if (result == null) {
			logInvalid(keys, value, "targeter");
			return fallback;
		}
		
		return result;
	}
	
	/**
	 * Gets the Targeter associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Targeter value of argument, or fallback if none exists
	 */
	public Targeter getTargeter(boolean required, Targeter fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		Targeter result = TargeterFactory.build(value);
		if (result == null) {
			logInvalid(keys, value, "targeter");
			return fallback;
		}
		
		return result;
	}
	
	/**
	 * Gets the function associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.<br>
	 * Will never return null. If fallback is null, a null-object will be returned.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found, or null for null-object
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Function value of argument, fallback if none exists, or null object if fallback is null
	 */
	public FunctionRunner getFunction(boolean required, FunctionRunner fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback == null ? new FunctionRunner(null) : fallback;
		
		return new FunctionRunner(value);
	}

	/**
	 * Gets the rbg color associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Color value of argument, or fallback if none exists
	 */
	public Color getColor(boolean required, Color fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		Color result = ColorParser.build(value);
		if (result == null) {
			logInvalid(keys, value, "RGB color");
			return fallback;
		}
		
		return result;
	}
	
	/**
	 * Gets the BlockPattern associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return BlockPattern value of argument, or fallback if none exists
	 */
	public BlockPattern getBlockPattern(boolean required, BlockPattern fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		BlockPattern result = BlockPattern.buildFromString(value);
		if (result == null) {
			//Continuing the error message given by BlockPattern
			GrandLogger.log("  In: " + lineString, LogType.CONFIG_ERRORS);
			
			return fallback;
		}
		
		return result;
	}
	
	/**
	 * Gets the BlockMask associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return BlockMask value of argument, or fallback if none exists
	 */
	public BlockMask getBlockMask(boolean required, BlockMask fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		BlockMask result = BlockMask.buildFromString(value);
		if (result == null) {
			//Continuing the error message given by BlockMask
			GrandLogger.log("  In: " + lineString, LogType.CONFIG_ERRORS);
			
			return fallback;
		}
		
		return result;
	}
	
	/**
	 * Gets the enum type associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid type name.<br>
	 * <br>
	 * Unlike the other argument getters, this function cannot use a null fallback. If one is desired, there is an overload<br>
	 * which takes a class instead of a fallback value and uses null as a fallback.
	 * 
	 * @param required Whether or not a value is required
	 * @param fallback Non-null value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Enum type value of argument, or fallback if none exists
	 */
	public <T extends Enum<T>> T getEnum(boolean required, T fallback, String... keys) {
		//Get value from map
		String lookupName = findValue(required, keys);
		if (lookupName == null) return fallback;
		
		//Find enum from value
		Class<T> enumClass = fallback.getDeclaringClass();
		lookupName = lookupName.toUpperCase();
		T type = StringTools.parseEnum(lookupName, enumClass);
		if (type != null) return type;
		
		//Invalid enum type name
		logInvalid(keys, lookupName, enumClass.getSimpleName() + " enum");
		return fallback;
	}
	
	/**
	 * Gets the enum type associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid type name.<br>
	 * <br>
	 * This is an overload of the main enum argument getter which gives a null fallback value.
	 * 
	 * @param required Whether or not a value is required
	 * @param enumClass Class declaring the enum type
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Enum type value of argument, or null if none exists
	 */
	public <T extends Enum<T>> T getEnum(boolean required, Class<T> enumClass, String... keys) {
		//Get value from map
		String lookupName = findValue(required, keys);
		if (lookupName == null) return null;
		
		//Find enum from value
		lookupName = lookupName.toUpperCase();
		T type = StringTools.parseEnum(lookupName, enumClass);
		if (type != null) return type;
		
		//Invalid enum type name
		logInvalid(keys, lookupName, enumClass.getSimpleName() + " enum");
		return null;
	}
	
	private String findValue(boolean required, String[] keys) {
		//Get value from map
		String result = null;
		for (String key : keys) {
			result = argumentMap.remove(key == null ? null : key.toLowerCase());
			if (result != null) break;
		}
		//Not found
		if (result == null || result.isEmpty()) {
			if (required) {
				GrandLogger.log("Missing required value for \"" + keys[0] + "\".", LogType.CONFIG_ERRORS);
				GrandLogger.log("  In: " + lineString, LogType.CONFIG_ERRORS);
				
				String keysString = keys[0];
				for (int i = 1; i < keys.length; i++) keysString = keysString + ", " + keys[i];
				GrandLogger.log("  Aliases: " + keysString, LogType.CONFIG_ERRORS);
			}
			return null;
		}
		//Return result
		return result;
	}
	
	private void logInvalid(String[] keys, String value, String expected) {
		GrandLogger.log("Value \"" + value + "\" for \"" + keys[0] + "\" is invalid (Expected " + expected + ").", LogType.CONFIG_ERRORS);
		GrandLogger.log("  In: " + lineString, LogType.CONFIG_ERRORS);
		
		String keysString = keys[0];
		for (int i = 1; i < keys.length; i++) keysString = keysString + ", " + keys[i];
		GrandLogger.log("  Aliases: " + keysString, LogType.CONFIG_ERRORS);
	}
	
}
