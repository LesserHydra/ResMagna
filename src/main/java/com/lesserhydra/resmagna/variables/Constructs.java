package com.lesserhydra.resmagna.variables;

import com.lesserhydra.bukkitutil.ExpUtils;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.util.MathUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Constructs {
	
	/* NONE - Typically symbolizes an error
	 */
	public static final ValueConstruct NONE = new ValueConstruct() {
		@Override public boolean mayHave(ValueType type) { return false; }
		@Override public boolean isNull() { return true; }
		@Override public Value evaluate(Target target) { return Values.NONE; }
		@Override public boolean isSettable() { return true; }
		@Override public void set(Target target, Value value) {}
	};
	
	/* HEALTH - LivingEntity health
	 */
	public static final ValueConstruct HEALTH = new ValueConstruct.WithLivingEntitySettable() {
		@Override public boolean mayHave(ValueType type) { return ValueType.NUMBER.has(type); }
		
		@Override public Value get(LivingEntity target) { return Values.wrap(target.getHealth()); }
		
		@Override public void set(LivingEntity target, Value value) {
			if (!value.hasFloat()) {
				GrandLogger.log("Tried to set health construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			double normalValue = MathUtil.clamp(value.asDouble(),
					0.0, target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			target.setHealth(normalValue);
		}
	};
	
	/* FIRE_TICKS - Entity fire ticks
	 */
	public static final ValueConstruct FIRE_TICKS = new ValueConstruct.WithLivingEntitySettable() {
		@Override public boolean mayHave(ValueType type) { return ValueType.NUMBER.has(type); }
		
		@Override public Value get(LivingEntity target) { return Values.wrap(target.getFireTicks()); }
		
		@Override public void set(LivingEntity target, Value value) {
			if (!value.hasInteger()) {
				GrandLogger.log("Tried to set fire ticks construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			int normalValue = MathUtil.clamp(value.asInteger(), 0, target.getMaxFireTicks());
			target.setFireTicks(normalValue);
		}
	};
	
	/* HUNGER - Player hunger
	 */
	public static final ValueConstruct HUNGER = new ValueConstruct.WithPlayerSettable() {
		@Override public boolean mayHave(ValueType type) { return ValueType.NUMBER.has(type); }
		
		@Override public Value get(Player target) {
			return Values.wrap(target.getFoodLevel() + target.getSaturation());
		}
		
		@Override public void set(Player target, Value value) {
			if (!value.hasFloat()) {
				GrandLogger.log("Tried to set hunger construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			
			int foodLevel = target.getFoodLevel();
			float saturation = target.getSaturation();
			float oldValue = foodLevel + saturation;
			float newValue = MathUtil.clamp(value.asFloat(), 0.0f, 40.0f);
			
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
	public static final ValueConstruct FOOD_LEVEL = new ValueConstruct.WithPlayerSettable() {
		@Override public boolean mayHave(ValueType type) { return ValueType.NUMBER.has(type); }
		
		@Override public Value get(Player target) { return Values.wrap(target.getFoodLevel()); }
		
		@Override public void set(Player target, Value value) {
			if (!value.hasInteger()) {
				GrandLogger.log("Tried to set food level construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			int normalValue = MathUtil.clamp(value.asInteger(), 0, 20);
			target.setFoodLevel(normalValue);
		}
	};
	
	/* SATURATION - Player saturation
	 */
	public static final ValueConstruct SATURATION = new ValueConstruct.WithPlayerSettable() {
		@Override public boolean mayHave(ValueType type) { return ValueType.NUMBER.has(type); }
		
		@Override public Value get(Player target) { return Values.wrap(target.getSaturation()); }
		
		@Override public void set(Player target, Value value) {
			if (!value.hasFloat()) {
				GrandLogger.log("Tried to set saturation construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			float normalValue = MathUtil.clamp(value.asFloat(), 0.0f, 20.0f);
			target.setSaturation(normalValue);
		}
	};
	
	/* EXHAUSTION - Player exhaustion
	 */
	public static final ValueConstruct EXHAUSTION = new ValueConstruct.WithPlayerSettable() {
		@Override public boolean mayHave(ValueType type) { return ValueType.NUMBER.has(type); }
		
		@Override public Value get(Player target) { return Values.wrap(target.getExhaustion()); }
		
		@Override public void set(Player target, Value value) {
			if (!value.hasFloat()) {
				GrandLogger.log("Tried to set exhaustion construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			float normalValue = Math.max(0.0f, value.asFloat());
			target.setExhaustion(normalValue);
		}
	};
	
	/* TOTAL_EXP - Player total experience
	 */
	public static final ValueConstruct TOTAL_EXP = new ValueConstruct.WithPlayerSettable() {
		@Override public boolean mayHave(ValueType type) { return ValueType.NUMBER.has(type); }
		
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
			int normalValue = Math.max(0, value.asInteger());
			Pair<Integer, Float> result = ExpUtils.getLevelAndProgress(normalValue);
			target.setLevel(result.getLeft());
			target.setExp(result.getRight());
		}
	};
	
	/* EXP - Player experience progress
	 */
	public static final ValueConstruct EXP = new ValueConstruct.WithPlayerSettable() {
		@Override public boolean mayHave(ValueType type) { return ValueType.NUMBER.has(type); }
		
		@Override public Value get(Player target) { return Values.wrap(target.getExp()); }
		
		@Override public void set(Player target, Value value) {
			if (!value.hasFloat()) {
				GrandLogger.log("Tried to set exp construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			float normalValue = MathUtil.clamp(value.asFloat(), 0.0f, 1.0f);
			target.setExp(normalValue);
		}
	};
	
	/* LEVELS - Player levels
	 */
	public static final ValueConstruct LEVELS = new ValueConstruct.WithPlayerSettable() {
		@Override public boolean mayHave(ValueType type) { return ValueType.NUMBER.has(type); }
		
		@Override public Value get(Player target) { return Values.wrap(target.getLevel()); }
		
		@Override public void set(Player target, Value value) {
			if (!value.hasInteger()) {
				GrandLogger.log("Tried to set levels construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			int normalValue = Math.max(0, value.asInteger());
			target.setLevel(normalValue);
		}
	};

	
}
