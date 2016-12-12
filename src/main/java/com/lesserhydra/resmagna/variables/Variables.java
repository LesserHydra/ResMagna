package com.lesserhydra.resmagna.variables;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class Variables {
	public static Variable NONE = new VarNone();
	
	public static Variable wrap(boolean data) { return new VarBoolean(data); }
	public static Variable wrap(int data) { return new VarInteger(data); }
	public static Variable wrap(double data) { return new VarDouble(data); }
	public static Variable wrap(Location data) { return data != null ? new VarLocation(data.clone()) : NONE; }
	public static Variable wrap(LivingEntity data) { return data != null ? new VarLivingEntity(data) : NONE; }
	
	private static class VarNone implements Variable {
		@Override public boolean isNull() { return true; }
	}
	
	private static class VarBoolean implements Variable {
		private final boolean data;
		private VarBoolean(boolean data) { this.data = data; }
		
		@Override public boolean hasBoolean() { return true; }
		@Override public boolean getBoolean() { return data; }
	}
	
	private static class VarInteger implements Variable {
		private final int data;
		private VarInteger(int data) { this.data = data; }
		
		@Override public boolean hasInteger() { return true; }
		@Override public boolean hasDouble() { return true; }
		@Override public int getInteger() { return data; }
		@Override public double getDouble() { return data; }
		
		@Override public Variable add(Variable other) { return wrap(data + other.getInteger()); }
		@Override public Variable subtract(Variable other) { return wrap(data - other.getInteger()); }
		@Override public Variable multiply(Variable other) { return wrap(data * other.getInteger()); }
		@Override public Variable divide(Variable other) { return wrap(data / other.getInteger()); }
		@Override public Variable modulus(Variable other) { return wrap(data % other.getInteger()); }
	}
	
	private static class VarDouble implements Variable {
		private final double data;
		private VarDouble(double data) { this.data = data; }
		
		@Override public boolean hasInteger() { return true; }
		@Override public boolean hasDouble() { return true; }
		@Override public int getInteger() { return (int) data; }
		@Override public double getDouble() { return data; }
		
		@Override public Variable add(Variable other) { return wrap(data + other.getDouble()); }
		@Override public Variable subtract(Variable other) { return wrap(data - other.getDouble()); }
		@Override public Variable multiply(Variable other) { return wrap(data * other.getDouble()); }
		@Override public Variable divide(Variable other) { return wrap(data / other.getDouble()); }
		@Override public Variable modulus(Variable other) { return wrap(data % other.getDouble()); }
	}
	
	private static class VarLocation implements Variable {
		private final Location data;
		private VarLocation(@NotNull Location data) { this.data = data; }
		
		@Override public boolean hasLocation() { return true; }
		@Override @NotNull public Location getLocation() { return data; }
	}
	
	private static class VarLivingEntity implements Variable {
		private final LivingEntity data;
		private VarLivingEntity(@NotNull LivingEntity data) { this.data = data; }
		
		@Override public boolean hasEntity() { return true; }
		@Override public boolean hasLocation() { return true; }
		@Override @NotNull public LivingEntity getEntity() { return data; }
		@Override @NotNull public Location getLocation() { return data.getLocation(); }
	}
}
