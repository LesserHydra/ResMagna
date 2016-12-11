package com.lesserhydra.resmagna.variables;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class Variables {
	public static Variable NONE = new VarNone();
	
	public static Variable wrap(boolean data) { return new VarBoolean(data); }
	public static Variable wrap(@NotNull Number data) { return new VarNumber(data); }
	public static Variable wrap(Location data) { return data != null ? new VarLocation(data) : NONE; }
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
	
	private static class VarNumber implements Variable {
		private final Number data;
		private VarNumber(@NotNull Number data) { this.data = data; }
		
		@Override public boolean hasNumber() { return true; }
		@Override @NotNull public Number getNumber() { return data; }
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
