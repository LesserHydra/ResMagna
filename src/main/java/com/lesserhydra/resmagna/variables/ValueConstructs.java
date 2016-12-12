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

public class ValueConstructs {
	
	/* NONE - Typically symbolizes an error
	 */
	public static final ValueConstruct NONE = new ValueConstruct() {
		@Override public boolean isNull() { return true; }
		@Override public Value get(Target target) { return Values.NONE; }
		@Override public boolean isSettable() { return true; }
		@Override public void set(Target target, Value value) {}
	};
	
	/* HEALTH - LivingEntity health
	 */
	private static final ValueConstruct HEALTH = new ValueConstruct.WithLivingEntitySettable() {
		@Override public Value get(LivingEntity target) { return Values.wrap(target.getHealth()); }
		
		@Override public void set(LivingEntity target, Value value) {
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
	private static final ValueConstruct HUNGER = new ValueConstruct.WithPlayerSettable() {
		@Override public Value get(Player target) {
			return Values.wrap(target.getFoodLevel() + target.getSaturation());
		}
		
		@Override public void set(Player target, Value value) {
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
	private static final ValueConstruct FOOD_LEVEL = new ValueConstruct.WithPlayerSettable() {
		@Override public Value get(Player target) { return Values.wrap(target.getFoodLevel()); }
		
		@Override public void set(Player target, Value value) {
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
	private static final ValueConstruct SATURATION = new ValueConstruct.WithPlayerSettable() {
		@Override public Value get(Player target) { return Values.wrap(target.getSaturation()); }
		
		@Override public void set(Player target, Value value) {
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
	private static final ValueConstruct EXHAUSTION = new ValueConstruct.WithPlayerSettable() {
		@Override public Value get(Player target) { return Values.wrap(target.getExhaustion()); }
		
		@Override public void set(Player target, Value value) {
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
	private static final ValueConstruct TOTAL_EXP = new ValueConstruct.WithPlayerSettable() {
		@Override public Value get(Player target) {
			int result = ExpUtils.calculateXpFromLevel(target.getLevel())
					+ ExpUtils.calculateXpFromProgress(target.getLevel(), target.getExp());
			return Values.wrap(result);
		}
		
		@Override public void set(Player target, Value value) {
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
	private static final ValueConstruct EXP = new ValueConstruct.WithPlayerSettable() {
		@Override public Value get(Player target) { return Values.wrap(target.getExp()); }
		
		@Override public void set(Player target, Value value) {
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
	private static final ValueConstruct LEVELS = new ValueConstruct.WithPlayerSettable() {
		@Override public Value get(Player target) { return Values.wrap(target.getLevel()); }
		
		@Override public void set(Player target, Value value) {
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
	public static ValueConstruct makeSettable(Function<Target, Value> getter, BiConsumer<Target, Value> setter) {
		return new Settable(getter, setter);
	}
	
	/**
	 * Parse construct string.
	 * @param string Construct string
	 * @return Resulting construct
	 */
	public static ValueConstruct parse(String string) {
		if (StringTools.isInteger(string)) return t -> Values.wrap(Integer.parseInt(string));
		else if (StringTools.isFloat(string)) return t -> Values.wrap(Double.parseDouble(string));
		else return parseMap(string);
	}
	
	private static ValueConstruct parseMap(String string) {
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
				//GrandLogger.log("Global Variable: " + string, LogType.DEBUG);
				return VariableHandler.linkConstruct(string);
		}
	}
	
	private static class Settable implements ValueConstruct {
		
		private final Function<Target, Value> getter;
		private final BiConsumer<Target, Value> setter;
		
		Settable(Function<Target, Value> getter, BiConsumer<Target, Value> setter) {
			this.getter = getter;
			this.setter = setter;
		}
		
		@Override
		public Value get(Target target) { return getter.apply(target); }
		
		@Override
		public boolean isSettable() { return true; }
		
		@Override
		public void set(Target target, Value value) { setter.accept(target, value); }
	}
	
}
