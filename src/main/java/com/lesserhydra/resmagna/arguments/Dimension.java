package com.lesserhydra.resmagna.arguments;

import org.bukkit.Bukkit;
import org.bukkit.World;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;

enum Dimension {
	
	SAME		(null),
	OVERWORLD	(""),
	NETHER		("_nether"),
	END			("_the_end");
	
	private final String worldSuffix;
	
	private Dimension(String worldSuffix) {
		this.worldSuffix = worldSuffix;
	}
	
	public World getWorld(World currentWorld) {
		if (this == SAME) return currentWorld;
		String baseWorldName = getBaseWorldName(currentWorld.getName());
		return Bukkit.getWorld(baseWorldName + worldSuffix);
	}
	
	public static Dimension fromString(String string) {
		if (string == null) return SAME;
		string = string.toUpperCase();
		
		for (Dimension type : values()) {
			if (string.equals(type.name())) return type;
		}
		
		GrandLogger.log("Ignoring invalid dimension name: " + string, LogType.CONFIG_ERRORS);
		return SAME;
	}
	
	private String getBaseWorldName(String worldName) {
		if (worldName.endsWith("_nether")) return worldName.replace("_nether", "");
		if (worldName.endsWith("_the_end")) return worldName.replace("_the_end", "");
		return worldName;
	}
	
}
