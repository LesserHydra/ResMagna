package com.lesserhydra.resmagna.arguments;

import com.lesserhydra.resmagna.configuration.GrandAbilityHandler;
import com.lesserhydra.resmagna.configuration.GroupingParser;
import com.lesserhydra.resmagna.function.Functor;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Targeter;
import com.lesserhydra.resmagna.variables.ConstructFactory;
import com.lesserhydra.resmagna.variables.ValueConstruct;
import com.lesserhydra.resmagna.variables.ValueType;
import com.lesserhydra.resmagna.variables.Values;
import com.lesserhydra.util.StringTools;
import org.bukkit.Color;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("SameParameterValue")
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
	
	public void logExtra() {
		if (argumentMap.isEmpty()) return;
		GrandLogger.log("Unused arguments: ", LogType.CONFIG_ERRORS);
		GrandLogger.log("  " + lineString, LogType.CONFIG_ERRORS);
		argumentMap.entrySet()
				.forEach(e -> GrandLogger.log("  " + e.getKey() + " : " + e.getValue(), LogType.CONFIG_ERRORS));
	}
	
	@NotNull
	public Evaluators.ForString getString(boolean required, String fallback, String... keys) {
		return getString(required, new Evaluators.ForString(ConstructFactory.makeLiteral(Values.wrap(fallback))), keys);
	}
	
	@NotNull
	public Evaluators.ForString getString(boolean required, Evaluators.ForString fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		ValueConstruct result = ConstructFactory.parse(value, ValueType.STRING);
		if (!result.mayHave(ValueType.STRING)) {
			logInvalid(keys, value, "string");
			return fallback;
		}
		
		return new Evaluators.ForString(result);
	}
	
	/**
	 * Gets the boolean associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Boolean value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForBoolean getBoolean(boolean required, boolean fallback, String... keys) {
		return getBoolean(required, new Evaluators.ForBoolean(ConstructFactory.makeLiteral(Values.wrap(fallback))), keys);
		
	}
	
	/**
	 * Gets the boolean associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Boolean value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForBoolean getBoolean(boolean required, Evaluators.ForBoolean fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		ValueConstruct result = ConstructFactory.parse(value, ValueType.BOOLEAN);
		if (!result.mayHave(ValueType.BOOLEAN)) {
			logInvalid(keys, value, "boolean");
			return fallback;
		}
		
		return new Evaluators.ForBoolean(result);
	}
	
	/**
	 * Gets the integer associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Integer value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForInt getInteger(boolean required, int fallback, String... keys) {
		return getInteger(required, new Evaluators.ForInt(ConstructFactory.makeLiteral(Values.wrap(fallback))), keys);
	}
	
	/**
	 * Gets the integer associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Integer value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForInt getInteger(boolean required, Evaluators.ForInt fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		ValueConstruct result = ConstructFactory.parse(value, ValueType.NUMBER);
		if (!result.mayHave(ValueType.NUMBER)) {
			logInvalid(keys, value, "integer");
			return fallback;
		}
		
		return new Evaluators.ForInt(result);
	}
	
	/**
	 * Gets the integer associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Integer value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForLong getLong(boolean required, long fallback, String... keys) {
		return getLong(required, new Evaluators.ForLong(ConstructFactory.makeLiteral(Values.wrap(fallback))), keys);
	}
	
	/**
	 * Gets the long associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Long value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForLong getLong(boolean required, Evaluators.ForLong fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		ValueConstruct result = ConstructFactory.parse(value, ValueType.NUMBER);
		if (!result.mayHave(ValueType.NUMBER)) {
			logInvalid(keys, value, "long");
			return fallback;
		}
		
		return new Evaluators.ForLong(result);
	}
	
	/**
	 * Gets the float associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Float value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForFloat getFloat(boolean required, float fallback, String... keys) {
		return getFloat(required, new Evaluators.ForFloat(ConstructFactory.makeLiteral(Values.wrap(fallback))), keys);
	}
	
	/**
	 * Gets the float associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Float value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForFloat getFloat(boolean required, Evaluators.ForFloat fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		ValueConstruct result = ConstructFactory.parse(value, ValueType.NUMBER);
		if (!result.mayHave(ValueType.NUMBER)) {
			logInvalid(keys, value, "float");
			return fallback;
		}
		
		return new Evaluators.ForFloat(result);
	}
	
	/**
	 * Gets the float associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Double value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForDouble getDouble(boolean required, double fallback, String... keys) {
		return getDouble(required, new Evaluators.ForDouble(ConstructFactory.makeLiteral(Values.wrap(fallback))), keys);
	}
	
	/**
	 * Gets the double associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Double value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForDouble getDouble(boolean required, Evaluators.ForDouble fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		ValueConstruct result = ConstructFactory.parse(value, ValueType.NUMBER);
		if (!result.mayHave(ValueType.NUMBER)) {
			logInvalid(keys, value, "double");
			return fallback;
		}
		
		return new Evaluators.ForDouble(result);
	}
	
	/**
	 * Gets the GrandLocation associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return GrandLocation value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForLocation getLocation(boolean required, GrandLocation fallback, String... keys) {
		return getLocation(required, new Evaluators.ForLocation(ConstructFactory.make(fallback)), keys);
	}
	
	/**
	 * Gets the GrandLocation associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return GrandLocation value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForLocation getLocation(boolean required, Evaluators.ForLocation fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		ValueConstruct result = ConstructFactory.parse(value, ValueType.LOCATION);
		if (!result.mayHave(ValueType.LOCATION)) {
			logInvalid(keys, value, "location");
			return fallback;
		}
		
		return new Evaluators.ForLocation(result);
	}
	
	/**
	 * Gets the Entity associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Entity value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForEntity getEntity(boolean required, Targeter fallback, String... keys) {
		return getEntity(required, new Evaluators.ForEntity(ConstructFactory.make(fallback)), keys);
	}
	
	/**
	 * Gets the Entity associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Entity value of argument, or fallback if none exists
	 */
	@NotNull
	public Evaluators.ForEntity getEntity(boolean required, Evaluators.ForEntity fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		ValueConstruct result = ConstructFactory.parse(value, ValueType.ENTITY);
		if (!result.mayHave(ValueType.ENTITY)) {
			logInvalid(keys, value, "entity");
			return fallback;
		}
		
		return new Evaluators.ForEntity(result);
	}
	
	@NotNull
	public Evaluators.ForBlockMask getBlockMask(boolean required, BlockMask fallback, String... keys) {
		return getBlockMask(required, new Evaluators.ForBlockMask(ConstructFactory.makeLiteral(Values.wrap(fallback))), keys);
	}
	
	@NotNull
	public Evaluators.ForBlockMask getBlockMask(boolean required, Evaluators.ForBlockMask fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		//TODO: Continue the error message given by BlockMask
		ValueConstruct result = ConstructFactory.parse(value, ValueType.BLOCK_MASK);
		if (!result.mayHave(ValueType.BLOCK_MASK)) {
			logInvalid(keys, value, "block mask");
			return fallback;
		}
		
		return new Evaluators.ForBlockMask(result);
	}
	
	@NotNull
	public Evaluators.ForBlockPattern getBlockPattern(boolean required, BlockPattern fallback, String... keys) {
		return getBlockPattern(required, new Evaluators.ForBlockPattern(ConstructFactory.makeLiteral(Values.wrap(fallback))), keys);
	}
	
	@NotNull
	public Evaluators.ForBlockPattern getBlockPattern(boolean required, Evaluators.ForBlockPattern fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		//TODO: Continue the error message given by BlockPattern
		ValueConstruct result = ConstructFactory.parse(value, ValueType.BLOCK_PATTERN);
		if (!result.mayHave(ValueType.BLOCK_PATTERN)) {
			logInvalid(keys, value, "block pattern");
			return fallback;
		}
		
		return new Evaluators.ForBlockPattern(result);
	}
	
	/**
	 * Gets the function associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.<br>
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Function value of argument, or fallback if none exists
	 */
	@Contract("_, !null, _ -> !null")
	public Functor getFunction(boolean required, Functor fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		return GrandAbilityHandler.getInstance().requestFunction(value);
	}

	/**
	 * Gets the rbg color associated with the given key.<br>
	 * Logs an error if required and none found, or if invalid format.
	 * @param required Whether or not a value is required
	 * @param fallback Value to default to if none found
	 * @param keys Keys to search under, null for default (no key provided)
	 * @return Color value of argument, or fallback if none exists
	 */
	@Contract("_, !null, _ -> !null")
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
	
	@Contract("_, !null, _ -> !null")
	public PotionEffectType getPotionEffectType(boolean required, PotionEffectType fallback, String... keys) {
		String value = findValue(required, keys);
		if (value == null) return fallback;
		
		PotionEffectType result = PotionEffectType.getByName(value);
		if (result == null) {
			logInvalid(keys, value, "potion effect type");
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
	@NotNull
	public <T extends Enum<T>> T getEnum(boolean required, @NotNull T fallback, String... keys) {
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
	@Nullable
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
