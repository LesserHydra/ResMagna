package com.lesserhydra.resmagna.variables;

import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface VariableConstruct {
	
	Variable get(Target target);
	default boolean isNull() { return false; }
	default boolean isSettable() { return false; }
	default void set(Target target, Variable variable) {
		throw new UnsupportedOperationException("Variable construct is not settable. Check isSettable() first.");
	}
	
	interface WithLivingEntity extends VariableConstruct {
		@Override default Variable get(Target target) {
			if (!target.isEntity()) {
				GrandLogger.log("Tried to access a LivingEntity variable with invalid target.", LogType.RUNTIME_ERRORS);
				return Variables.NONE;
			}
			return get(target.asEntity());
		}
		Variable get(LivingEntity target);
	}
	
	interface WithLivingEntitySettable extends VariableConstruct {
		@Override default boolean isSettable() { return true; }
		
		@Override default Variable get(Target target) {
			if (!target.isEntity()) {
				GrandLogger.log("Tried to access a LivingEntity variable with invalid target.", LogType.RUNTIME_ERRORS);
				return Variables.NONE;
			}
			return get(target.asEntity());
		}
		Variable get(LivingEntity target);
		
		@Override default void set(Target target, Variable value) {
			if (!target.isPlayer()) {
				GrandLogger.log("Tried to access a LivingEntity variable with invalid target.", LogType.RUNTIME_ERRORS);
				return;
			}
			set(target.asEntity(), value);
		}
		void set(LivingEntity target, Variable value);
	}
	
	interface WithPlayer extends VariableConstruct {
		@Override default Variable get(Target target) {
			if (!target.isPlayer()) {
				GrandLogger.log("Tried to access a player variable with non-player target.", LogType.RUNTIME_ERRORS);
				return Variables.NONE;
			}
			return get(target.asPlayer());
		}
		Variable get(Player target);
	}
	
	interface WithPlayerSettable extends VariableConstruct {
		@Override default boolean isSettable() { return true; }
		
		@Override default Variable get(Target target) {
			if (!target.isPlayer()) {
				GrandLogger.log("Tried to access a player variable with non-player target.", LogType.RUNTIME_ERRORS);
				return Variables.NONE;
			}
			return get(target.asPlayer());
		}
		Variable get(Player target);
		
		@Override default void set(Target target, Variable value) {
			if (!target.isPlayer()) {
				GrandLogger.log("Tried to access a player variable with non-player target.", LogType.RUNTIME_ERRORS);
				return;
			}
			set(target.asPlayer(), value);
		}
		void set(Player target, Variable value);
	}
	
}
