package com.lesserhydra.resmagna.variables;

import com.lesserhydra.resmagna.arguments.BlockMask;
import com.lesserhydra.resmagna.arguments.BlockPattern;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class Values {
	public static final Value NONE = new VarNone();
	
	public static Value wrap(boolean data) { return new VarBoolean(data); }
	public static Value wrap(int data) { return new VarInteger(data); }
	public static Value wrap(double data) { return new VarDouble(data); }
	public static Value wrap(String data) { return new VarString(data); }
	public static Value wrap(Location data) { return data != null ? new VarLocation(data.clone()) : NONE; }
	public static Value wrap(Entity data) {
		assert !(data instanceof LivingEntity);
		return data != null ? new VarEntity(data) : NONE;
	}
	public static Value wrap(LivingEntity data) { return data != null ? new VarLivingEntity(data) : NONE; }
	public static Value wrap(BlockMask data) { return new VarBlockMask(data); }
	public static Value wrap(BlockPattern data) { return new VarBlockPattern(data); }
	
	private static class VarNone implements Value {
		@Override public ValueType getType() { return ValueType.NONE; }
		@Override public boolean isNull() { return true; }
		
		@Override public String toString() { return "NONE"; }
	}
	
	private static class VarBoolean implements Value {
		private final boolean data;
		private VarBoolean(boolean data) { this.data = data; }
		
		@Override public ValueType getType() { return ValueType.BOOLEAN; }
		@Override public boolean hasBoolean() { return true; }
		@Override public boolean asBoolean() { return data; }
		
		@Override public String toString() { return Boolean.toString(data); }
	}
	
	private static class VarInteger implements Value {
		private final int data;
		private VarInteger(int data) { this.data = data; }
		
		@Override public ValueType getType() { return ValueType.NUMBER; }
		@Override public boolean hasInteger() { return true; }
		@Override public boolean hasFloat() { return true; }
		@Override public int asInteger() { return data; }
		@Override public long asLong() { return data; }
		@Override public float asFloat() { return data; }
		@Override public double asDouble() { return data; }
		
		@Override @NotNull public Value add(Value other) { return wrap(data + other.asInteger()); }
		@Override @NotNull public Value subtract(Value other) { return wrap(data - other.asInteger()); }
		@Override @NotNull public Value multiply(Value other) { return wrap(data * other.asInteger()); }
		@Override @NotNull public Value divide(Value other) { return wrap(data / other.asInteger()); }
		@Override @NotNull public Value modulus(Value other) { return wrap(data % other.asInteger()); }
		
		@Override public String toString() { return Integer.toString(data); }
	}
	
	private static class VarDouble implements Value {
		private final double data;
		private VarDouble(double data) { this.data = data; }
		
		@Override public ValueType getType() { return ValueType.NUMBER; }
		@Override public boolean hasInteger() { return true; }
		@Override public boolean hasFloat() { return true; }
		@Override public int asInteger() { return (int) data; }
		@Override public long asLong() { return (long) data; }
		@Override public float asFloat() { return (float) data; }
		@Override public double asDouble() { return data; }
		
		@Override @NotNull public Value add(Value other) { return wrap(data + other.asDouble()); }
		@Override @NotNull public Value subtract(Value other) { return wrap(data - other.asDouble()); }
		@Override @NotNull public Value multiply(Value other) { return wrap(data * other.asDouble()); }
		@Override @NotNull public Value divide(Value other) { return wrap(data / other.asDouble()); }
		@Override @NotNull public Value modulus(Value other) { return wrap(data % other.asDouble()); }
		
		@Override public String toString() { return Double.toString(data); }
	}
	
	private static class VarString implements Value {
		private final String data;
		private VarString(String data) { this.data = data; }
		@Override public ValueType getType() { return ValueType.STRING; }
		@Override public String toString() { return data; }
	}
	
	private static class VarLocation implements Value {
		private final Location data;
		private VarLocation(@NotNull Location data) { this.data = data; }
		
		@Override public ValueType getType() { return ValueType.LOCATION; }
		@Override public boolean hasLocation() { return true; }
		@Override @NotNull public Location asLocation() { return data.clone(); }
		
		@Override public String toString() { return data.toString(); }
	}
	
	private static class VarEntity implements Value {
		private final Entity data;
		private VarEntity(@NotNull Entity data) { this.data = data; }
		
		@Override public ValueType getType() { return ValueType.ENTITY; }
		@Override public boolean hasBaseEntity() { return true; }
		@Override public boolean hasLocation() { return true; }
		@Override @NotNull public Entity asBaseEntity() { return data; }
		@Override @NotNull public Location asLocation() { return data.getLocation(); }
		
		@Override public String toString() { return data.toString(); }
	}
	
	private static class VarLivingEntity implements Value {
		private final LivingEntity data;
		private VarLivingEntity(@NotNull LivingEntity data) { this.data = data; }
		
		@Override public ValueType getType() { return ValueType.LIVING; }
		@Override public boolean hasBaseEntity() { return true; }
		@Override public boolean hasEntity() { return true; }
		@Override public boolean hasLocation() { return true; }
		@Override @NotNull public Entity asBaseEntity() { return data; }
		@Override @NotNull public LivingEntity asEntity() { return data; }
		@Override @NotNull public Location asLocation() { return data.getLocation(); }
		
		@Override public String toString() { return data.toString(); }
	}
	
	private static class VarBlockMask implements Value {
		private final BlockMask data;
		private VarBlockMask(@NotNull BlockMask data) { this.data = data; }
		
		@Override public ValueType getType() { return ValueType.BLOCK_MASK; }
		@Override public boolean hasBlockMask() { return true; }
		@Override @NotNull public BlockMask asBlockMask() { return data; }
		
		@Override public String toString() { return data.toString(); }
	}
	
	private static class VarBlockPattern implements Value {
		private final BlockPattern data;
		private VarBlockPattern(@NotNull BlockPattern data) { this.data = data; }
		
		@Override public ValueType getType() { return ValueType.BLOCK_PATTERN; }
		@Override public boolean hasBlockPattern() { return true; }
		@Override @NotNull public BlockPattern asBlockPattern() { return data; }
		
		@Override public String toString() { return data.toString(); }
	}
	
}
