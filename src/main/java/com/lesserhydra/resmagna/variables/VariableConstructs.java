package com.lesserhydra.resmagna.variables;

import com.lesserhydra.bukkitutil.ExpUtils;
import com.lesserhydra.resmagna.VariableHandler;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.util.MathUtil;
import com.lesserhydra.util.StringTools;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class VariableConstructs {
	
	/* NONE - Typically symbolizes an error
	 */
	public static final VariableConstruct NONE = new VariableConstruct() {
		@Override public boolean isNull() { return true; }
		@Override public Variable get(Target target) { return Variables.NONE; }
		@Override public boolean isSettable() { return true; }
		@Override public void set(Target target, Variable variable) {}
	};
	
	/* HEALTH - LivingEntity health
	 */
	private static final VariableConstruct HEALTH = new VariableConstruct.WithLivingEntitySettable() {
		@Override public Variable get(LivingEntity target) { return Variables.wrap(target.getHealth()); }
		
		@Override public void set(LivingEntity target, Variable value) {
			if (!value.hasDouble()) {
				GrandLogger.log("Tried to set health construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			double normalValue = MathUtil.clamp(value.getDouble(), 0.0, target.getMaxHealth());
			target.setHealth(normalValue);
		}
	};
	
	/* HUNGER - Player hunger
	 */
	private static final VariableConstruct HUNGER = new VariableConstruct.WithPlayerSettable() {
		@Override public Variable get(Player target) {
			return Variables.wrap(target.getFoodLevel() + target.getSaturation());
		}
		
		@Override public void set(Player target, Variable value) {
			if (!value.hasDouble()) {
				GrandLogger.log("Tried to set hunger construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			
			int foodLevel = target.getFoodLevel();
			float saturation = target.getSaturation();
			float oldValue = foodLevel + saturation;
			float newValue = (float) MathUtil.clamp(value.getDouble(), 0.0, 40.0);
			
			float diff = newValue - oldValue;
			
			//Subtract
			if (diff < 0) {
				float newSaturation = saturation + diff;
				float remaining = Math.min(newSaturation, 0.0f);
				newSaturation = Math.max(0.0f, newSaturation);
				
				int newFoodLevel = Math.max(0, foodLevel + (int)remaining);
				
				target.setFoodLevel(newFoodLevel);
				target.setSaturation(newSaturation);
			}
			//Add
			else {
				float newFoodLevel = foodLevel + diff;
				float remaining = Math.max(0.0f, newFoodLevel - 20.0f);
				newFoodLevel = Math.min(newFoodLevel, 20.0f);
				
				float newSaturation = Math.min(saturation + remaining, 20.0f);
				
				target.setFoodLevel((int) newFoodLevel);
				target.setSaturation(newSaturation);
			}
		}
	};
	
	/* FOOD_LEVEL - Player food level
	 */
	private static final VariableConstruct FOOD_LEVEL = new VariableConstruct.WithPlayerSettable() {
		@Override public Variable get(Player target) { return Variables.wrap(target.getFoodLevel()); }
		
		@Override public void set(Player target, Variable value) {
			if (!value.hasInteger()) {
				GrandLogger.log("Tried to set food level construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			int normalValue = MathUtil.clamp(value.getInteger(), 0, 20);
			target.setFoodLevel(normalValue);
		}
	};
	
	/* SATURATION - Player saturation
	 */
	private static final VariableConstruct SATURATION = new VariableConstruct.WithPlayerSettable() {
		@Override public Variable get(Player target) { return Variables.wrap(target.getSaturation()); }
		
		@Override public void set(Player target, Variable value) {
			if (!value.hasDouble()) {
				GrandLogger.log("Tried to set saturation construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			float normalValue = (float) MathUtil.clamp(value.getDouble(), 0.0, 20.0);
			target.setSaturation(normalValue);
		}
	};
	
	/* EXHAUSTION - Player exhaustion
	 */
	private static final VariableConstruct EXHAUSTION = new VariableConstruct.WithPlayerSettable() {
		@Override public Variable get(Player target) { return Variables.wrap(target.getExhaustion()); }
		
		@Override public void set(Player target, Variable value) {
			if (!value.hasDouble()) {
				GrandLogger.log("Tried to set exhaustion construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			float normalValue = (float) Math.max(0.0, value.getDouble());
			target.setExhaustion(normalValue);
		}
	};
	
	/* TOTAL_EXP - Player total experience
	 */
	private static final VariableConstruct TOTAL_EXP = new VariableConstruct.WithPlayerSettable() {
		@Override public Variable get(Player target) {
			int result = ExpUtils.calculateXpFromLevel(target.getLevel())
					+ ExpUtils.calculateXpFromProgress(target.getLevel(), target.getExp());
			return Variables.wrap(result);
		}
		
		@Override public void set(Player target, Variable value) {
			if (!value.hasInteger()) {
				GrandLogger.log("Tried to set total experience construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			int normalValue = Math.max(0, value.getInteger());
			Pair<Integer, Float> result = ExpUtils.getLevelAndProgress(normalValue);
			target.setLevel(result.getLeft());
			target.setExp(result.getRight());
		}
	};
	
	/* EXP - Player experience progress
	 */
	private static final VariableConstruct EXP = new VariableConstruct.WithPlayerSettable() {
		@Override public Variable get(Player target) { return Variables.wrap(target.getExp()); }
		
		@Override public void set(Player target, Variable value) {
			if (!value.hasDouble()) {
				GrandLogger.log("Tried to set exp construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			float normalValue = (float) MathUtil.clamp(value.getDouble(), 0.0, 1.0);
			target.setExp(normalValue);
		}
	};
	
	/* LEVELS - Player levels
	 */
	private static final VariableConstruct LEVELS = new VariableConstruct.WithPlayerSettable() {
		@Override public Variable get(Player target) { return Variables.wrap(target.getLevel()); }
		
		@Override public void set(Player target, Variable value) {
			if (!value.hasInteger()) {
				GrandLogger.log("Tried to set levels construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			int normalValue = Math.max(0, value.getInteger());
			target.setLevel(normalValue);
		}
	};
	
	/**
	 * Makes a settable variable construct with getter and setter functions.
	 * @param getter Getter function
	 * @param setter Setter function
	 * @return Resulting construct
	 */
	public static VariableConstruct makeSettable(Function<Target, Variable> getter, BiConsumer<Target, Variable> setter) {
		return new Settable(getter, setter);
	}
	
	/**
	 * Parse construct string.
	 * @param string Construct string
	 * @return Resulting construct
	 */
	public static VariableConstruct parse(String string) {
		if (StringTools.isInteger(string)) return t -> Variables.wrap(Integer.parseInt(string));
		else if (StringTools.isFloat(string)) return t -> Variables.wrap(Double.parseDouble(string));
		else return parseMap(string);
	}
	
	private static VariableConstruct parseMap(String string) {
		switch (string.toLowerCase()) {
			case "health": return HEALTH;
			case "hunger": return HUNGER;
			case "food": return FOOD_LEVEL;
			case "saturation": return SATURATION;
			case "exhaustion": return EXHAUSTION;
			case "totalexp": return TOTAL_EXP;
			case "exp": return EXP;
			case "levels": return LEVELS;
			
			default:
				//GrandLogger.log("Variable: " + string, LogType.DEBUG);
				return VariableHandler.linkConstruct(string);
		}
	}
	
	private static class Settable implements VariableConstruct {
		
		private final Function<Target, Variable> getter;
		private final BiConsumer<Target, Variable> setter;
		
		Settable(Function<Target, Variable> getter, BiConsumer<Target, Variable> setter) {
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
