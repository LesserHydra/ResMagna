package com.roboboy.PraedaGrandis.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.roboboy.PraedaGrandis.PraedaGrandis;

public abstract class MultiConfig
{
	protected PraedaGrandis plugin;
	
	protected File configFolder;
	protected Map<String, FileConfiguration> configs = new HashMap<String, FileConfiguration>();
	
	protected MultiConfig(PraedaGrandis p, File folder)
	{
		plugin = p;
		configFolder = folder;
	}
	
	public void reload()
	{
		configs.clear();
		for (File file : configFolder.listFiles())
		{
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			configs.put(file.getName(), config);
			loadConfig(config);
		}
	}

	protected abstract void loadConfig(FileConfiguration config);
}
