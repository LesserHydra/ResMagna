package com.roboboy.PraedaGrandis.Configuration;

import java.util.HashMap;
import java.util.Map;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Tools;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class AbilityArguments
{
	private final Map<String, String> argumentMap = new HashMap<>();
	
	public AbilityArguments(String argumentString) {
		//TODO: Rewrite argument parsing
		ConfigString parsedString = new ConfigString(argumentString.substring(1, argumentString.length()), ',');
		for (String s : parsedString) {
			String[] splitS = s.trim().split("=");
			argumentMap.put(splitS[0].trim(), splitS[1].trim());
		}
	}
	
	/**
	 * Gets the string associated with the given key. Logs an error if required and none found.
	 * @param key All-lowercase key
	 * @param fallback Value to default to if none found
	 * @param required Whether or not a value is required
	 * @return Raw string value of argument, or fallback if none exists
	 */
	public String get(String key, String fallback, boolean required) {
		String result = argumentMap.get(key);
		if (result == null) {
			result = fallback;
			if (required) PraedaGrandis.plugin.logger.log("Missing required value for \"" + key + "\".", LogType.CONFIG_ERRORS);
		}
		return result;
	}
	
	/**
	 * Gets the boolean associated with the given key. Logs an error if required and none found, or if invalid format.
	 * @param key All-lowercase key
	 * @param fallback Value to default to if none found
	 * @param required Whether or not a value is required
	 * @return Boolean value of argument, or fallback if none exists
	 */
	public boolean getBoolean(String key, boolean fallback, boolean required) {
		String value = argumentMap.get(key);
		
		if (value == null) {
			if (required) PraedaGrandis.plugin.logger.log("Missing required value for \"" + key + "\".", LogType.CONFIG_ERRORS);
			return fallback;
		}
		
		if (!Tools.isBoolean(value)) {
			PraedaGrandis.plugin.logger.log("Value \"" + value + "\" for \"" + key + "\" is invalid (Expected boolean).", LogType.CONFIG_ERRORS);
			return fallback;
		}
		
		return Boolean.parseBoolean(value);
	}
	
	/**
	 * Gets the integer associated with the given key. Logs an error if required and none found, or if invalid format.
	 * @param key All-lowercase key
	 * @param fallback Value to default to if none found
	 * @param required Whether or not a value is required
	 * @return Integer value of argument, or fallback if none exists
	 */
	public int getInteger(String key, int fallback, boolean required) {
		String value = argumentMap.get(key);
		
		if (value == null) {
			if (required) PraedaGrandis.plugin.logger.log("Missing required value for \"" + key + "\".", LogType.CONFIG_ERRORS);
			return fallback;
		}
		
		if (!Tools.isInteger(value)) {
			PraedaGrandis.plugin.logger.log("Value \"" + value + "\" for \"" + key + "\" is invalid (Expected integer).", LogType.CONFIG_ERRORS);
			return fallback;
		}
		
		return Integer.parseInt(value);
	}
	
	/**
	 * Gets the long associated with the given key. Logs an error if required and none found, or if invalid format.
	 * @param key All-lowercase key
	 * @param fallback Value to default to if none found
	 * @param required Whether or not a value is required
	 * @return Long value of argument, or fallback if none exists
	 */
	public long getLong(String key, long fallback, boolean required) {
		String value = argumentMap.get(key);
		
		if (value == null) {
			if (required) PraedaGrandis.plugin.logger.log("Missing required value for \"" + key + "\".", LogType.CONFIG_ERRORS);
			return fallback;
		}
		
		if (!Tools.isInteger(value)) {
			PraedaGrandis.plugin.logger.log("Value \"" + value + "\" for \"" + key + "\" is invalid (Expected long).", LogType.CONFIG_ERRORS);
			return fallback;
		}
		
		return Long.parseLong(value);
	}
	
	/**
	 * Gets the float associated with the given key. Logs an error if required and none found, or if invalid format.
	 * @param key All-lowercase key
	 * @param fallback Value to default to if none found
	 * @param required Whether or not a value is required
	 * @return Float value of argument, or fallback if none exists
	 */
	public float getFloat(String key, float fallback, boolean required) {
		String value = argumentMap.get(key);
		
		if (value == null) {
			if (required) PraedaGrandis.plugin.logger.log("Missing required value for \"" + key + "\".", LogType.CONFIG_ERRORS);
			return fallback;
		}
		
		if (!Tools.isFloat(value)) {
			PraedaGrandis.plugin.logger.log("Value \"" + value + "\" for \"" + key + "\" is invalid (Expected float).", LogType.CONFIG_ERRORS);
			return fallback;
		}
		
		return Float.parseFloat(value);
	}
	
	/**
	 * Gets the double associated with the given key. Logs an error if required and none found, or if invalid format.
	 * @param key All-lowercase key
	 * @param fallback Value to default to if none found
	 * @param required Whether or not a value is required
	 * @return Double value of argument, or fallback if none exists
	 */
	public double getDouble(String key, double fallback, boolean required) {
		String value = argumentMap.get(key);
		
		if (value == null) {
			if (required) PraedaGrandis.plugin.logger.log("Missing required value for \"" + key + "\".", LogType.CONFIG_ERRORS);
			return fallback;
		}
		
		if (!Tools.isFloat(value)) {
			PraedaGrandis.plugin.logger.log("Value \"" + value + "\" for \"" + key + "\" is invalid (Expected double).", LogType.CONFIG_ERRORS);
			return fallback;
		}
		
		return Double.parseDouble(value);
	}
	
	/**
	 * Gets the GrandLocation associated with the given key. Logs an error if required and none found, or if invalid format.
	 * @param key All-lowercase key
	 * @param fallback Value to default to if none found
	 * @param required Whether or not a value is required
	 * @return GrandLocation value of argument, or fallback if none exists
	 */
	public GrandLocation getLocation(String key, GrandLocation fallback, boolean required) {
		String value = argumentMap.get(key);
		
		if (value == null) {
			if (required) PraedaGrandis.plugin.logger.log("Missing required value for \"" + key + "\".", LogType.CONFIG_ERRORS);
			return fallback;
		}
		
		if (!Tools.isLocation(value)) {
			PraedaGrandis.plugin.logger.log("Value \"" + value + "\" for \"" + key + "\" is invalid (Expected location).", LogType.CONFIG_ERRORS);
			return fallback;
		}
		
		return new GrandLocation(value);
	}
}
