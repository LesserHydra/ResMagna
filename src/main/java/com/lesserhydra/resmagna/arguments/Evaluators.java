package com.lesserhydra.resmagna.arguments;

import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.variables.Value;
import com.lesserhydra.resmagna.variables.ValueConstruct;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

//TODO: Log error if invalid, but only log NONE if require == true
public class Evaluators {
	
	public static class ForString {
		private final ValueConstruct construct;
		private String value = "";
		ForString(ValueConstruct construct) { this.construct = construct; }
		
		public String get() { return value; }
		public boolean evaluate(Target target) { return evaluate(target, true); }
		public boolean evaluate(Target target, boolean require) {
			Value result = construct.evaluate(target);
			if (!result.hasString()) {
				if (require) GrandLogger.log("String expected, got " + result + ".", LogType.RUNTIME_ERRORS);
				return false;
			}
			value = result.asString();
			return true;
		}
	}
	
	public static class ForBoolean {
		private final ValueConstruct construct;
		private boolean value = false;
		ForBoolean(ValueConstruct construct) { this.construct = construct; }
		
		public boolean get() { return value; }
		public boolean evaluate(Target target) { return evaluate(target, true); }
		public boolean evaluate(Target target, boolean require) {
			Value result = construct.evaluate(target);
			if (!result.hasBoolean()) {
				if (require) GrandLogger.log("Boolean expected, got " + result + ".", LogType.RUNTIME_ERRORS);
				return false;
			}
			value = result.asBoolean();
			return true;
		}
	}
	
	public static class ForInt {
		private final ValueConstruct construct;
		private int value = 0;
		ForInt(ValueConstruct construct) { this.construct = construct; }
		
		public int get() { return value; }
		public boolean evaluate(Target target) { return evaluate(target, true); }
		public boolean evaluate(Target target, boolean require) {
			Value result = construct.evaluate(target);
			if (!result.hasInteger()) {
				if (require) GrandLogger.log("Integer expected, got " + result + ".", LogType.RUNTIME_ERRORS);
				return false;
			}
			value = result.asInteger();
			return true;
		}
	}
	
	public static class ForLong {
		private final ValueConstruct construct;
		private long value = 0;
		ForLong(ValueConstruct construct) { this.construct = construct; }
		
		public long get() { return value; }
		public boolean evaluate(Target target) { return evaluate(target, true); }
		public boolean evaluate(Target target, boolean require) {
			Value result = construct.evaluate(target);
			if (!result.hasInteger()) {
				if (require) GrandLogger.log("Long integer expected, got " + result + ".", LogType.RUNTIME_ERRORS);
				return false;
			}
			value = result.asLong();
			return true;
		}
	}
	
	public static class ForFloat {
		private final ValueConstruct construct;
		private float value = 0;
		ForFloat(ValueConstruct construct) { this.construct = construct; }
		
		public float get() { return value; }
		public boolean evaluate(Target target) { return evaluate(target, true); }
		public boolean evaluate(Target target, boolean require) {
			Value result = construct.evaluate(target);
			if (!result.hasFloat()) {
				if (require) GrandLogger.log("Float expected, got " + result + ".", LogType.RUNTIME_ERRORS);
				return false;
			}
			value = result.asFloat();
			return true;
		}
	}
	
	public static class ForDouble {
		private final ValueConstruct construct;
		private double value = 0;
		ForDouble(ValueConstruct construct) { this.construct = construct; }
		
		public double get() { return value; }
		public boolean evaluate(Target target) { return evaluate(target, true); }
		public boolean evaluate(Target target, boolean require) {
			Value result = construct.evaluate(target);
			if (!result.hasFloat()) {
				if (require) GrandLogger.log("Double expected, got " + result + ".", LogType.RUNTIME_ERRORS);
				return false;
			}
			value = result.asDouble();
			return true;
		}
	}
	
	public static class ForLocation {
		private final ValueConstruct construct;
		private Location value = null;
		ForLocation(ValueConstruct construct) { this.construct = construct; }
		
		public Location get() { return value; }
		public boolean evaluate(Target target) { return evaluate(target, true); }
		public boolean evaluate(Target target, boolean require) {
			Value result = construct.evaluate(target);
			if (!result.hasLocation()) {
				if (require) GrandLogger.log("Location expected, got " + result + ".", LogType.RUNTIME_ERRORS);
				return false;
			}
			value = result.asLocation();
			return true;
		}
	}
	
	public static class ForEntity {
		private final ValueConstruct construct;
		private Entity value = null;
		ForEntity(ValueConstruct construct) { this.construct = construct; }
		
		public Entity get() { return value; }
		public boolean evaluate(Target target) { return evaluate(target, true); }
		public boolean evaluate(Target target, boolean require) {
			Value result = construct.evaluate(target);
			if (!result.hasBaseEntity()) {
				if (require) GrandLogger.log("Entity expected, got " + result + ".", LogType.RUNTIME_ERRORS);
				return false;
			}
			value = result.asBaseEntity();
			return true;
		}
	}
	
	public static class ForLivingEntity {
		private final ValueConstruct construct;
		private LivingEntity value = null;
		ForLivingEntity(ValueConstruct construct) { this.construct = construct; }
		
		public LivingEntity get() { return value; }
		public boolean evaluate(Target target) { return evaluate(target, true); }
		public boolean evaluate(Target target, boolean require) {
			Value result = construct.evaluate(target);
			if (!result.hasEntity()) {
				if (require) GrandLogger.log("LivingEntity expected, got " + result + ".", LogType.RUNTIME_ERRORS);
				return false;
			}
			value = result.asEntity();
			return true;
		}
	}
	
	public static class ForBlockMask {
		private final ValueConstruct construct;
		private BlockMask value = null;
		ForBlockMask(ValueConstruct construct) { this.construct = construct; }
		
		public BlockMask get() { return value; }
		public boolean evaluate(Target target) { return evaluate(target, true); }
		public boolean evaluate(Target target, boolean require) {
			Value result = construct.evaluate(target);
			if (!result.hasBlockMask()) {
				if (require) GrandLogger.log("Block mask expected, got " + result + ".", LogType.RUNTIME_ERRORS);
				return false;
			}
			value = result.asBlockMask();
			return true;
		}
	}
	
	public static class ForBlockPattern {
		private final ValueConstruct construct;
		private BlockPattern value = null;
		ForBlockPattern(ValueConstruct construct) { this.construct = construct; }
		
		public BlockPattern get() { return value; }
		public boolean evaluate(Target target) { return evaluate(target, true); }
		public boolean evaluate(Target target, boolean require) {
			Value result = construct.evaluate(target);
			if (!result.hasBlockPattern()) {
				if (require) GrandLogger.log("Block pattern expected, got " + result + ".", LogType.RUNTIME_ERRORS);
				return false;
			}
			value = result.asBlockPattern();
			return true;
		}
	}
	
}
