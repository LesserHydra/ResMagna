package com.lesserhydra.resmagna.variables;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class Values {
	public static Value NONE = new VarNone();
	
	public static Value wrap(boolean data) { return new VarBoolean(data); }
	public static Value wrap(int data) { return new VarInteger(data); }
	public static Value wrap(double data) { return new VarDouble(data); }
	public static Value wrap(Location data) { return data != null ? new VarLocation(data.clone()) : NONE; }
	public static Value wrap(LivingEntity data) { return data != null ? new VarLivingEntity(data) : NONE; }
	
	private static class VarNone implements Value {
		@Override public boolean isNull() { return true; }
	}
	
	private static class VarBoolean implements Value {
		private final boolean data;
		private VarBoolean(boolean data) { this.data = data; }
		
		@Override public boolean hasBoolean() { return true; }
		@Override public boolean getBoolean() { return data; }
	}
	
	private static class VarInteger implements Value {
		private final int data;
		private VarInteger(int data) { this.data = data; }
		
		@Override public boolean hasInteger() { return true; }
		@Override public boolean hasDouble() { return true; }
		@Override public int getInteger() { return data; }
		@Override public double getDouble() { return data; }
		
		@Override public Value add(Value other) { return wrap(data + other.getInteger()); }
		@Override public Value subtract(Value other) { return wrap(data - other.getInteger()); }
		@Override public Value multiply(Value other) { return wrap(data * other.getInteger()); }
		@Override public Value divide(Value other) { return wrap(data / other.getInteger()); }
		@Override public Value modulus(Value other) { return wrap(data % other.getInteger()); }
	}
	
	private static class VarDouble implements Value {
		private final double data;
		private VarDouble(double data) { this.data = data; }
		
		@Override public boolean hasInteger() { return true; }
		@Override public boolean hasDouble() { return true; }
		@Override public int getInteger() { return (int) data; }
		@Override public double getDouble() { return data; }
		
		@Override public Value add(Value other) { return wrap(data + other.getDouble()); }
		@Override public Value subtract(Value other) { return wrap(data - other.getDouble()); }
		@Override public Value multiply(Value other) { return wrap(data * other.getDouble()); }
		@Override public Value divide(Value other) { return wrap(data / other.getDouble()); }
		@Override public Value modulus(Value other) { return wrap(data % other.getDouble()); }
	}
	
	private static class VarLocation implements Value {
		private final Location data;
		private VarLocation(@NotNull Location data) { this.data = data; }
		
		@Override public boolean hasLocation() { return true; }
		@Override @NotNull public Location getLocation() { return data; }
	}
	
	private static class VarLivingEntity implements Value {
		private final LivingEntity data;
		private VarLivingEntity(@NotNull LivingEntity data) { this.data = data; }
		
		@Override public boolean hasEntity() { return true; }
		@Override public boolean hasLocation() { return true; }
		@Override @NotNull public LivingEntity getEntity() { return data; }
		@Override @NotNull public Location getLocation() { return data.getLocation(); }
	}
}
