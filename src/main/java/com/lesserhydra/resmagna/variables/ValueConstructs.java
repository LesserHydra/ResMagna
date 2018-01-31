package com.lesserhydra.resmagna.variables;

import com.lesserhydra.bukkitutil.BlockUtil;
import com.lesserhydra.bukkitutil.ExpUtils;
import com.lesserhydra.resmagna.VariableHandler;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeter;
import com.lesserhydra.resmagna.targeters.TargeterFactory;
import com.lesserhydra.util.MathUtil;
import com.lesserhydra.util.StringTools;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
	private static final ValueConstruct FIRE_TICKS = new ValueConstruct.WithLivingEntitySettable() {
		@Override public Value get(LivingEntity target) { return Values.wrap(target.getFireTicks()); }
		
		@Override public void set(LivingEntity target, Value value) {
			if (!value.hasInteger()) {
				GrandLogger.log("Tried to set fire ticks construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			//FIXME: Why is max set at 20???
			//int normalValue = MathUtil.clamp(value.asInteger(), 0, target.getMaxFireTicks());
			target.setFireTicks(value.asInteger());
		}
	};
	
	/* HUNGER - Player hunger
	 */
	private static final ValueConstruct HUNGER = new ValueConstruct.WithPlayerSettable() {
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
	private static final ValueConstruct FOOD_LEVEL = new ValueConstruct.WithPlayerSettable() {
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
	private static final ValueConstruct SATURATION = new ValueConstruct.WithPlayerSettable() {
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
	private static final ValueConstruct EXHAUSTION = new ValueConstruct.WithPlayerSettable() {
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
			int normalValue = Math.max(0, value.asInteger());
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
	private static final ValueConstruct LEVELS = new ValueConstruct.WithPlayerSettable() {
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
	
	/* RAIN - Number of ticks till rain is toggled
	 * If == 0, rain state will be kept, and a random duration will be applied.
	 * If <= 0, rain is inactive, value is time till activated
	 * If > 0, rain is active, value is time till deactivated
	 */
	private static final ValueConstruct RAIN = new ValueConstruct.WithLocationSettable() {
		/* Number of ticks till rain is toggled
         * If <= 0, random duration will be set.
         * If == 1, will toggle rain state. (Next tick will randomize duration)
         * If > 0, value decrements every tick.
         */
		@Override public Value get(Location target) {
			World world = target.getWorld();
			boolean isRaining = world.hasStorm();
			int rainDuration = world.getWeatherDuration();
			return Values.wrap(isRaining ? rainDuration : -rainDuration);
		}
		
		@Override public void set(Location target, Value value) {
			if (!value.hasInteger()) {
				GrandLogger.log("Tried to set rain construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			int intValue = value.asInteger();
			World world = target.getWorld();
			boolean shouldHaveRain = intValue > 0;
			int rainDuration = Math.abs(intValue);
			if (intValue != 0 && shouldHaveRain != world.hasStorm()) world.setStorm(shouldHaveRain);
			world.setWeatherDuration(rainDuration);
		}
	};
	
	/* THUNDER - Number of ticks till thunder is toggled
	 * If == 0, thunder state will be kept, and a random duration will be applied.
	 * If <= 0, thunder is inactive, value is time till activated
	 * If > 0, thunder is active, value is time till deactivated
	 */
	private static final ValueConstruct THUNDER = new ValueConstruct.WithLocationSettable() {
		/* Number of ticks till thunder is toggled
         * If <= 0, random duration will be set.
         * If == 1, will toggle thunder state. (Next tick will randomize duration)
         * If > 0, value decrements every tick.
         */
		@Override public Value get(Location target) {
			World world = target.getWorld();
			boolean isThundering = world.isThundering();
			int thunderDuration = world.getThunderDuration();
			return Values.wrap(isThundering ? thunderDuration : -thunderDuration);
		}
		
		@Override public void set(Location target, Value value) {
			if (!value.hasInteger()) {
				GrandLogger.log("Tried to set thunder construct to invalid value.", LogType.RUNTIME_ERRORS);
				return;
			}
			int intValue = value.asInteger();
			World world = target.getWorld();
			boolean shouldHaveThunder = intValue > 0;
			int thunderDuration = Math.abs(intValue);
			if (intValue != 0 && shouldHaveThunder != world.isThundering()) world.setThundering(shouldHaveThunder);
			world.setThunderDuration(thunderDuration);
		}
	};
	
	/* TEMPERATURE - True temperature of block (affected by height)
	 */
	private static final ValueConstruct.WithLocation TEMPERATURE
			= target -> Values.wrap(BlockUtil.getTrueTemperature(target.getBlock()));
	
	/* TOTAL_LIGHT_LEVEL - Light level
	 */
	private static final ValueConstruct.WithLocation TOTAL_LIGHT_LEVEL
			= target -> Values.wrap(target.getBlock().getLightLevel());
	
	/* BLOCK_LIGHT_LEVEL - Light level from blocks
	 */
	private static final ValueConstruct.WithLocation BLOCK_LIGHT_LEVEL
			= target -> Values.wrap(target.getBlock().getLightFromBlocks());
	
	/* SKY_LIGHT_LEVEL - Light level from sky
	 */
	private static final ValueConstruct.WithLocation SKY_LIGHT_LEVEL
			= target -> Values.wrap(target.getBlock().getLightFromSky());
	
	public static ValueConstruct makeLiteral(Value value) {
		return t -> value;
	}
	
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
	@NotNull
	public static ValueConstruct parse(String string) {
		char firstChar = string.charAt(0);
		if (firstChar == '"') return parseString(string);
		else if (firstChar == '@') return parseTargeter(string);
		else if (StringTools.isInteger(string)) return makeLiteral(Values.wrap(Integer.parseInt(string)));
		else if (StringTools.isFloat(string)) return makeLiteral(Values.wrap(Double.parseDouble(string)));
		else return parseMap(string);
	}
	
	@NotNull
	private static ValueConstruct parseString(String string) {
		//TODO: Proper
		return makeLiteral(Values.wrap(string.substring(1, string.length()-1)));
	}
	
	@NotNull
	private static ValueConstruct parseTargeter(String string) {
		Targeter result = TargeterFactory.build(string);
		return result != null ? t -> result.getRandomTarget(t).current() : NONE;
	}
	
	private static ValueConstruct parseMap(String string) {
		switch (string.toLowerCase()) {
			case "health": return HEALTH;
			case "fireticks": return FIRE_TICKS;
			case "hunger": return HUNGER;
			case "food": return FOOD_LEVEL;
			case "saturation": return SATURATION;
			case "exhaustion": return EXHAUSTION;
			case "totalexp": return TOTAL_EXP;
			case "exp": return EXP;
			case "levels": return LEVELS;
			case "rain": return RAIN;
			case "thunder": return THUNDER;
			case "temperature": return TEMPERATURE;
			case "totallightlevel": return TOTAL_LIGHT_LEVEL;
			case "blocklightlevel": return BLOCK_LIGHT_LEVEL;
			case "skylightlevel": return SKY_LIGHT_LEVEL;
			
			default:
				GrandLogger.log("Global Variable: " + string, LogType.CONFIG_PARSING);
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
