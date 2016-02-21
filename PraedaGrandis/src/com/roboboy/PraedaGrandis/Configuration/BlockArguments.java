package com.roboboy.PraedaGrandis.Configuration;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Color;
import com.roboboy.PraedaGrandis.Tools;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class BlockArguments
{
	//(\w+)\s*=\s*([^,;\s\n\(]*(?:\s*\((\$[\d]+)\))?)
	static private final Pattern argumentPattern = Pattern.compile("(\\w+)\\s*=\\s*([^,;\\s\\n\\(]*(?:\\s*\\((\\$[\\d]+)\\))?)");
	
	private final String lineString;
	private final Map<String, String> argumentMap = new HashMap<>();
	
	public BlockArguments(String argumentString, String lineString) {
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
	 * Gets the string associated with the given key. Logs an error if required and none found.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param key All-lowercase key
	 * @return Raw string value of argument, or fallback if none exists
	 */
	public String getString(boolean required, String fallback, String... keys) {
		String result = findValue(required, keys);
		if (result == null) return fallback;
		return result;
	}
	
	/**
	 * Gets the boolean associated with the given key. Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param key All-lowercase key
	 * @return Boolean value of argument, or fallback if none exists
	 */
	public boolean getBoolean(boolean required, boolean fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		if (!Tools.isBoolean(value)) {
			logInvalid(keys, value, "boolean");
			return fallback;
		}
		
		return Boolean.parseBoolean(value);
	}
	
	/**
	 * Gets the integer associated with the given key. Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param key All-lowercase key
	 * @return Integer value of argument, or fallback if none exists
	 */
	public int getInteger(boolean required, int fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		if (!Tools.isInteger(value)) {
			logInvalid(keys, value, "integer");
			return fallback;
		}
		
		return Integer.parseInt(value);
	}
	
	/**
	 * Gets the long associated with the given key. Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param key All-lowercase key
	 * @return Long value of argument, or fallback if none exists
	 */
	public long getLong(boolean required, long fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		if (!Tools.isInteger(value)) {
			logInvalid(keys, value, "long");
			return fallback;
		}
		
		return Long.parseLong(value);
	}
	
	/**
	 * Gets the float associated with the given key. Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param key All-lowercase key
	 * @return Float value of argument, or fallback if none exists
	 */
	public float getFloat(boolean required, float fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		if (!Tools.isFloat(value)) {
			logInvalid(keys, value, "float");
			return fallback;
		}
		
		return Float.parseFloat(value);
	}
	
	/**
	 * Gets the double associated with the given key. Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param key All-lowercase key
	 * @return Double value of argument, or fallback if none exists
	 */
	public double getDouble(boolean required, double fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		if (!Tools.isFloat(value)) {
			logInvalid(keys, value, "double");
			return fallback;
		}
		
		return Double.parseDouble(value);
	}
	
	/**
	 * Gets the GrandLocation associated with the given key. Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param key All-lowercase key
	 * @return GrandLocation value of argument, or fallback if none exists
	 */
	public GrandLocation getLocation(boolean required, GrandLocation fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		//TODO: Log error
		return new GrandLocation(value.substring(1, value.length()-1));
	}
	
	/**
	 * Gets the Targeter associated with the given key. Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param key All-lowercase key
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
	 * Gets the enum type associated with the given key. Logs an error if required and none found, or if invalid type name.
	 * @param required Whether or not a value is required
	 * @param fallback Non-null value to default to if none found
	 * @param key All-lowercase key
	 * @return Enum type value of argument, or fallback if none exists
	 */
	public <T extends Enum<T>> T getEnum(boolean required, T fallback, String... keys) {
		//Get value from map
		String lookupName = findValue(required, keys);
		if (lookupName == null) return fallback;
		
		//Find enum from value
		Class<T> enumClass = fallback.getDeclaringClass();
		lookupName = lookupName.toUpperCase();
		for (T type : EnumSet.allOf(enumClass)) {
			if (lookupName.equals(type.name())) return type;
		}
		
		//Invalid enum type name
		logInvalid(keys, lookupName, enumClass.getSimpleName());
		return fallback;
	}
	
	private String findValue(boolean required, String[] keys) {
		//Get value from map
		String result = null;
		for (String key : keys) {
			result = argumentMap.get(key);
			if (result != null) break;
		}
		//Not found
		if (result == null || result.isEmpty()) {
			if (required) {
				GrandLogger.log("Missing required value for \"" + keys[0] + "\".", LogType.CONFIG_ERRORS);
				GrandLogger.log("  In: " + lineString, LogType.CONFIG_ERRORS);
				
				String keysString = keys[0];
				for (int i = 1; i < keys.length; i++) keysString = keysString + ", " + keys[i];
				GrandLogger.log("  Alias': " + keysString, LogType.CONFIG_ERRORS);
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
		GrandLogger.log("  Alias': " + keysString, LogType.CONFIG_ERRORS);
	}
	
}
