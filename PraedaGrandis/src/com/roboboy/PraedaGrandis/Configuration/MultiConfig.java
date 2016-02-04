package com.roboboy.PraedaGrandis.Configuration;

import java.io.File;
import java.io.FilenameFilter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * A class that handles all configuration files in a folder
 * @author roboboy
 */
abstract class MultiConfig
{
	private static final FilenameFilter YAML_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(".yaml") || name.endsWith(".yml"));
        }
    };
	
	/**
	 * Calls {@link #loadConfig(FileConfiguration) loadConfig} for every configuration file in the given folder<br>
	 * <br>
	 * <strong>Should only be called from within an implementation!</strong>
	 * @param configFolder
	 */
	protected final void reload(File configFolder) {
		for (File file : configFolder.listFiles(YAML_FILTER)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			loadConfig(config);
		}
	}
	
	/**
	 * Reloads all handled configuration files from scratch.<br>
	 * <br>
	 * <strong>Individual implementations may have prerequisites!</strong>
	 */
	public abstract void reload();
	
	/**
	 * Loads a single configuration file
	 * @param config FileConfiguration to load
	 */
	protected abstract void loadConfig(FileConfiguration config);
}
