package com.lesserhydra.resmagna.variables;

import com.lesserhydra.resmagna.VariableHandler;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.util.StringTools;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class VariableConstructs {
	
	public static final VariableConstruct NONE = new VariableConstruct() {
		@Override public boolean isNull() { return true; }
		@Override public Variable get(Target target) { return Variables.NONE; }
		@Override public boolean isSettable() { return true; }
		@Override public void set(Target target, Variable variable) {}
	};
	
	public static final VariableConstruct HEALTH = new VariableConstruct.WithLivingEntitySettable() {
		@Override public Variable get(LivingEntity target) { return Variables.wrap(target.getHealth()); }
		
		@Override public void set(LivingEntity target, Variable value) {
			if (!value.hasDouble()) {
				GrandLogger.log("Tried to set health construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			target.setHealth(value.getDouble());
		}
	};
	
	public static final VariableConstruct HUNGER = new VariableConstruct.WithPlayerSettable() {
		@Override public Variable get(Player target) { return Variables.wrap(target.getFoodLevel()); }
		
		@Override public void set(Player target, Variable value) {
			if (!value.hasInteger()) {
				GrandLogger.log("Tried to set hunger construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			target.setFoodLevel(value.getInteger());
		}
	};
	
	public static final VariableConstruct SATURATION = new VariableConstruct.WithPlayerSettable() {
		@Override public Variable get(Player target) { return Variables.wrap(target.getSaturation()); }
		
		@Override public void set(Player target, Variable value) {
			if (!value.hasInteger()) {
				GrandLogger.log("Tried to set saturation construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			target.setSaturation(value.getInteger());
		}
	};
	
	public static final VariableConstruct EXP = new VariableConstruct.WithPlayerSettable() {
		@Override public Variable get(Player target) { return Variables.wrap(target.getTotalExperience()); }
		
		@Override public void set(Player target, Variable value) {
			if (!value.hasInteger()) {
				GrandLogger.log("Tried to set exp construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			target.setTotalExperience(value.getInteger());
		}
	};
	
	public static final VariableConstruct LEVELS = new VariableConstruct.WithPlayerSettable() {
		@Override public Variable get(Player target) { return Variables.wrap(target.getLevel()); }
		
		@Override public void set(Player target, Variable value) {
			if (!value.hasInteger()) {
				GrandLogger.log("Tried to set levels construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			target.setLevel(value.getInteger());
		}
	};
	
	public static VariableConstruct makeSettable(Function<Target, Variable> getter, BiConsumer<Target, Variable> setter) {
		return new Settable(getter, setter);
	}
	
	public static VariableConstruct parse(String string) {
		if (StringTools.isInteger(string)) return t -> Variables.wrap(Integer.parseInt(string));
		else if (StringTools.isFloat(string)) return t -> Variables.wrap(Double.parseDouble(string));
		else return parseMap(string);
	}
	
	private static VariableConstruct parseMap(String string) {
		switch (string.toLowerCase()) {
			case "health": return HEALTH;
			case "hunger": return HUNGER;
			case "saturation": return SATURATION;
			case "exp": return EXP;
			case "levels": return LEVELS;
			default: return VariableHandler.linkConstruct(string);
		}
	}
	
	private static class Settable implements VariableConstruct {
		
		private final Function<Target, Variable> getter;
		private final BiConsumer<Target, Variable> setter;
		
		public Settable(Function<Target, Variable> getter, BiConsumer<Target, Variable> setter) {
			this.getter = getter;
			this.setter = setter;
		}
		
		@Override
		public Variable get(Target target) { return getter.apply(target); }
		
		@Override
		public boolean isSettable() { return true; }
		
		@Override
		public void set(Target target, Variable variable) { setter.accept(target, variable); }
	}
	
}
