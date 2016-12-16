package com.lesserhydra.resmagna.variables;

import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Represents a supplier (and potentially consumer) of values.
 */
public interface ValueConstruct {
	
	/**
	 * Checks if this construct has the potential to supply a value with the given type.
	 * Should only be used for compile-time error reporting, since the type may still be unknown.
	 * @param type Type required from this construct
	 * @return False if the required value type can never be returned from the get() method.
	 */
	boolean mayHave(ValueType type);
	Value evaluate(Target target);
	
	default boolean isNull() { return false; }
	default boolean isSettable() { return false; }
	default void set(Target target, Value value) {
		throw new UnsupportedOperationException("Value construct is not settable. Check isSettable() first.");
	}
	
	interface WithLivingEntity extends ValueConstruct {
		@Override default Value evaluate(Target target) {
			if (!target.isEntity()) {
				GrandLogger.log("Tried to access a LivingEntity variable with invalid target.", LogType.RUNTIME_ERRORS);
				return Values.NONE;
			}
			return get(target.asEntity());
		}
		Value get(LivingEntity target);
	}
	
	interface WithLivingEntitySettable extends ValueConstruct {
		@Override default boolean isSettable() { return true; }
		
		@Override default Value evaluate(Target target) {
			if (!target.isEntity()) {
				GrandLogger.log("Tried to access a LivingEntity variable with invalid target.", LogType.RUNTIME_ERRORS);
				return Values.NONE;
			}
			return get(target.asEntity());
		}
		Value get(LivingEntity target);
		
		@Override default void set(Target target, Value value) {
			if (!target.isPlayer()) {
				GrandLogger.log("Tried to access a LivingEntity variable with invalid target.", LogType.RUNTIME_ERRORS);
				return;
			}
			set(target.asEntity(), value);
		}
		void set(LivingEntity target, Value value);
	}
	
	interface WithPlayer extends ValueConstruct {
		@Override default Value evaluate(Target target) {
			if (!target.isPlayer()) {
				GrandLogger.log("Tried to access a player variable with non-player target.", LogType.RUNTIME_ERRORS);
				return Values.NONE;
			}
			return get(target.asPlayer());
		}
		Value get(Player target);
	}
	
	interface WithPlayerSettable extends ValueConstruct {
		@Override default boolean isSettable() { return true; }
		
		@Override default Value evaluate(Target target) {
			if (!target.isPlayer()) {
				GrandLogger.log("Tried to access a player variable with non-player target.", LogType.RUNTIME_ERRORS);
				return Values.NONE;
			}
			return get(target.asPlayer());
		}
		Value get(Player target);
		
		@Override default void set(Target target, Value value) {
			if (!target.isPlayer()) {
				GrandLogger.log("Tried to access a player variable with non-player target.", LogType.RUNTIME_ERRORS);
				return;
			}
			set(target.asPlayer(), value);
		}
		void set(Player target, Value value);
	}
	
}
