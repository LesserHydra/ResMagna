package com.lesserhydra.resmagna.variables;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface Variable {
	
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
	default boolean getBoolean() {
		throw new UnrepresentedTypeException("Variable does not represent a boolean value, use hasBoolean() first.");
	}
	
	/**
	 * Checks if this variable represents a numerical value.
	 * @return True if this represents a numerical value
	 */
	default boolean hasNumber() { return hasInteger() || hasDouble(); }
	
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
	default int getInteger() {
		throw new UnrepresentedTypeException("Variable does not represent an integer, use hasInteger() first.");
	}
	
	/**
	 * Checks if this variable represents a double value.
	 * @return True if this represents a double value
	 */
	default boolean hasDouble() { return false; }
	
	/**
	 * Gets the represented value as a double.
	 * @return The represented double
	 * @throws UnrepresentedTypeException If this does not represent a double
	 */
	default double getDouble() {
		throw new UnrepresentedTypeException("Variable does not represent a double, use hasDouble() first.");
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
	@NotNull default Location getLocation() {
		throw new UnrepresentedTypeException("Variable does not represent a Location, use hasLocation() first.");
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
	@NotNull default LivingEntity getEntity() {
		throw new UnrepresentedTypeException("Variable does not represent a LivingEntity, use hasEntity() first.");
	}
	
	
	default Variable add(Variable other) {
		throw new UnsupportedOperationException(""); //TODO
	}
	
	default Variable subtract(Variable other) {
		throw new UnsupportedOperationException(""); //TODO
	}
	
	default Variable multiply(Variable other) {
		throw new UnsupportedOperationException(""); //TODO
	}
	
	default Variable divide(Variable other) {
		throw new UnsupportedOperationException(""); //TODO
	}
	
	default Variable modulus(Variable other) {
		throw new UnsupportedOperationException(""); //TODO
	}
	
}
