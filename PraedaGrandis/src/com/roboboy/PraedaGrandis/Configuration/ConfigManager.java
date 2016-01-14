package com.roboboy.PraedaGrandis.Configuration;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import com.roboboy.PraedaGrandis.PraedaGrandis;

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
	
	public ConfigManager(PraedaGrandis p) {
		plugin = p;
	}
	
	/**
	 * Reloads the main configuration file.
	 */
	public void reload() {
		FileConfiguration pluginConfig = plugin.getConfig();
		
		pluginFolder = plugin.getDataFolder();
		if (!pluginFolder.exists()) pluginFolder.mkdir();
		
		itemFolder = new File(pluginFolder.toString() + File.separator + pluginConfig.getString("Folders.items", "Items"));
		abilityFolder = new File(pluginFolder.toString() + File.separator + pluginConfig.getString("Folders.abilities", "Abilities"));
		
		if (!itemFolder.exists()) itemFolder.mkdir();
		if (!abilityFolder.exists()) abilityFolder.mkdir();
	}
	
	public File getAbilityFolder() {
		return abilityFolder;
	}
	
	public File getItemFolder() {
		return itemFolder;
	}
}
