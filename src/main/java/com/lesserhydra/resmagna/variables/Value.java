package com.lesserhydra.resmagna.variables;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a dynamically typed value.
 */
public interface Value {
	
	/**
	 * Checks if this variable represents nothing.
	 * @return True if this represents nothing
	 */
	default boolean isNull() { return false; }
	
	/**
	 * Checks if this variable represents a boolean value.
	 * @return True if this represents a boolean value
	 */
	default boolean hasBoolean() { return false; }
	
	/**
	 * Gets the represented value as a boolean.
	 * @return The represented boolean value
	 * @throws UnrepresentedTypeException If this does not represent a boolean
	 */
	default boolean asBoolean() {
		throw new UnrepresentedTypeException("Value does not represent a boolean value. Use hasBoolean() first.");
	}
	
	/**
	 * Checks if this variable represents a numerical value.
	 * @return True if this represents a numerical value
	 */
	default boolean hasNumber() { return hasInteger() || hasFloat(); }
	
	/**
	 * Checks if this variable represents an integer value.
	 * @return True if this represents an integer value
	 */
	default boolean hasInteger() { return false; }
	
	/**
	 * Gets the represented value as an integer.
	 * @return The represented integer
	 * @throws UnrepresentedTypeException If this does not represent an integer
	 */
	default int asInteger() {
		throw new UnrepresentedTypeException("Value does not represent an integer. Use hasInteger() first.");
	}
	
	/**
	 * Checks if this variable represents a double value.
	 * @return True if this represents a double value
	 */
	default boolean hasFloat() { return false; }
	
	/**
	 * Gets the represented value as a float.
	 * @return The represented float
	 * @throws UnrepresentedTypeException If this does not represent a float
	 */
	default float asFloat() {
		throw new UnrepresentedTypeException("Value does not represent a float. Use hasFloat() first.");
	}
	
	/**
	 * Gets the represented value as a double.
	 * @return The represented double
	 * @throws UnrepresentedTypeException If this does not represent a double
	 */
	default double asDouble() {
		throw new UnrepresentedTypeException("Value does not represent a double. Use hasFloat() first.");
	}
	
	/**
	 * Checks if this variable represents a Location.
	 * @return True if this represents a Location
	 */
	default boolean hasLocation() { return false; }
	
	/**
	 * Gets the represented value as a Location.
	 * @return The represented Location
	 * @throws UnrepresentedTypeException If this does not represent a Location
	 */
	@NotNull default Location asLocation() {
		throw new UnrepresentedTypeException("Value does not represent a Location. Use hasLocation() first.");
	}
	
	/**
	 * Checks if this variable represents a LivingEntity.
	 * @return True if this represents a LivingEntity
	 */
	default boolean hasEntity() { return false; }
	
	/**
	 * Gets the represented value as a LivingEntity.
	 * @return The represented LivingEntity
	 * @throws UnrepresentedTypeException If this does not represent a LivingEntity
	 */
	@NotNull default LivingEntity asEntity() {
		throw new UnrepresentedTypeException("Value does not represent a LivingEntity. Use hasEntity() first.");
	}
	
	/**
	 * Adds this value with other value.
	 * @param other Other value
	 * @return Resulting value
	 */
	@NotNull default Value add(Value other) {
		throw new UnsupportedOperationException("Value does not support addition. Use isNumber() first.");
	}
	
	/**
	 * Subtracts this value from other value.
	 * @param other Other value
	 * @return Resulting value
	 */
	@NotNull default Value subtract(Value other) {
		throw new UnsupportedOperationException("Value does not support subtraction. Use isNumber() first.");
	}
	
	/**
	 * Multiplies this value with other value.
	 * @param other Other value
	 * @return Resulting value
	 */
	@NotNull default Value multiply(Value other) {
		throw new UnsupportedOperationException("Value does not support multiplication. Use isNumber() first.");
	}
	
	/**
	 * Divides this value by other value.
	 * @param other Other value
	 * @return Resulting value
	 */
	@NotNull default Value divide(Value other) {
		throw new UnsupportedOperationException("Value does not support division. Use isNumber() first.");
	}
	
	/**
	 * Mods this value by other value.
	 * @param other Other value
	 * @return Resulting value
	 */
	@NotNull default Value modulus(Value other) {
		throw new UnsupportedOperationException("Value does not support modding. Use isNumber() first.");
	}
	
}
