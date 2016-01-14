package com.roboboy.PraedaGrandis.Configuration;

import java.io.File;
import java.util.EnumSet;
import org.bukkit.configuration.file.FileConfiguration;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Logging.LogType;

/**
 * Loads the main configuration file
 * @author roboboy
 */
public class ConfigManager
{
	private PraedaGrandis plugin;
	
	private File pluginFolder;
	private File itemFolder;
	private File abilityFolder;
	
	private long timerHandlerDelay;
	
	private EnumSet<LogType> enabledLogTypes;
	
	public ConfigManager(PraedaGrandis p) {
		plugin = p;
	}
	
	/**
	 * Reloads the main configuration file.
	 */
	public void reload() {
		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		FileConfiguration pluginConfig = plugin.getConfig();
		
		pluginFolder = plugin.getDataFolder();
		if (!pluginFolder.exists()) pluginFolder.mkdir();
		
		//Folders
		itemFolder = new File(pluginFolder.toString() + File.separator + pluginConfig.getString("Folders.items", "Items"));
		abilityFolder = new File(pluginFolder.toString() + File.separator + pluginConfig.getString("Folders.abilities", "Abilities"));
		if (!itemFolder.exists()) itemFolder.mkdir();
		if (!abilityFolder.exists()) abilityFolder.mkdir();
		
		//Timer check delay
		timerHandlerDelay = pluginConfig.getLong("timerCheckDelay", 80L);
		
		//Enabled log types
		enabledLogTypes = EnumSet.noneOf(LogType.class);
		for (LogType type : LogType.values()) {
			if (pluginConfig.getBoolean("EnabledLogTypes." + type.name(), false)) enabledLogTypes.add(type);
		}
	}
	
	public File getAbilityFolder() {
		return abilityFolder;
	}
	
	public File getItemFolder() {
		return itemFolder;
	}

	public long getTimerHandlerDelay(){
		return timerHandlerDelay;
	}
	
	public EnumSet<LogType> getEnabledLogTypes() {
		return enabledLogTypes;
	}
}
