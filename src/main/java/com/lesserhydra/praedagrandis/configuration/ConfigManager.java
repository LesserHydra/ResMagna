package com.lesserhydra.praedagrandis.configuration;

import java.io.File;
import java.util.EnumSet;
import org.bukkit.configuration.file.FileConfiguration;
import com.lesserhydra.praedagrandis.PraedaGrandis;
import com.lesserhydra.praedagrandis.logging.LogType;

/**
 * Loads the main configuration file
 * @author roboboy
 */
public class ConfigManager
{	
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
		PraedaGrandis.plugin.saveDefaultConfig();
		PraedaGrandis.plugin.reloadConfig();
		FileConfiguration pluginConfig = PraedaGrandis.plugin.getConfig();
		
		pluginFolder = PraedaGrandis.plugin.getDataFolder();
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
		GrandAbilityHandler.getInstance().reload();
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
