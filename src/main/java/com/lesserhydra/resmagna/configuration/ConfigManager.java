package com.lesserhydra.resmagna.configuration;

import java.io.File;
import java.util.EnumSet;

import com.lesserhydra.resmagna.ResMagna;
import org.bukkit.configuration.file.FileConfiguration;
import com.lesserhydra.resmagna.logging.LogType;

/**
 * Loads the main configuration file
 * @author roboboy
 */
public class ConfigManager {
	
	private static ConfigManager instance = new ConfigManager();
	private ConfigManager() {}
	public static ConfigManager getInstance() {
		return instance;
	}
	
	private File pluginFolder;
	private File itemFolder;
	private File abilityFolder;
	
	private long timerHandlerDelay;
	
	private EnumSet<LogType> enabledLogTypes;
	
	/**
	 * Reloads the main configuration file.
	 */
	public void reload() {
		ResMagna.plugin.saveDefaultConfig();
		ResMagna.plugin.reloadConfig();
		FileConfiguration pluginConfig = ResMagna.plugin.getConfig();
		
		pluginFolder = ResMagna.plugin.getDataFolder();
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
		
		//MultiConfigs
		ScriptLoader.getInstance().reload();
		ItemHandler.getInstance().reload();
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
