package com.lesserhydra.resmagna.variables;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface Variable {
	
	/**
	 * Checks if this variable does not represent anything.
	 * @return True if this represents nothing
	 */
	default boolean isNull() { return false; };
	
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
	 * Checks if this variable represents a Number.
	 * @return True if this represents a Number
	 */
	default boolean hasNumber() { return false; }
	
	/**
	 * Gets the represented value as a Number.
	 * @return The represented Number
	 * @throws UnrepresentedTypeException If this does not represent a Number
	 */
	@NotNull default Number getNumber() {
		throw new UnrepresentedTypeException("Variable does not represent a number, use hasNumber() first.");
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
	
}
