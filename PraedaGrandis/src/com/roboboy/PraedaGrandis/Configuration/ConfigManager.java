package com.roboboy.PraedaGrandis.Configuration;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import com.roboboy.PraedaGrandis.PraedaGrandis;

public class ConfigManager
{
	private PraedaGrandis plugin;
	
	public final File pluginFolder;
	public final File itemFolder;
	public final File abilityFolder;
	
	public FileConfiguration pluginConfig;
	
	public ConfigManager(PraedaGrandis p)
	{
		plugin = p;
		pluginConfig = plugin.getConfig();
		
		pluginFolder = plugin.getDataFolder();
		if (!pluginFolder.exists()) pluginFolder.mkdir();
		
		itemFolder = new File(pluginFolder.toString() + File.separator + pluginConfig.getString("itemFolder", "Items"));
		abilityFolder = new File(pluginFolder.toString() + File.separator + pluginConfig.getString("abilityFolder", "Abilities"));
		
		if (!itemFolder.exists()) itemFolder.mkdir();
		if (!abilityFolder.exists()) abilityFolder.mkdir();
	}
}
