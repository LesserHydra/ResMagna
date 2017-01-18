package com.lesserhydra.resmagna.configuration;

import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * A class that handles all configuration files in a folder
 * @author roboboy
 */
abstract class MultiConfig {
	
	private static final FilenameFilter YAML_FILTER = (dir, name) -> (name.endsWith(".yaml") || name.endsWith(".yml"));
	
	/**
	 * Calls {@link #loadConfig(FileConfiguration) loadConfig} for every configuration file in the given folder<br>
	 * <br>
	 * <strong>Should only be called from within an implementation!</strong>
	 * @param configFolder
	 */
	protected final void reload(File configFolder) {
		File[] files = configFolder.listFiles(YAML_FILTER);
		if (files == null) {
			GrandLogger.log("Error reading files from \"" + configFolder.getName() + "\".", LogType.CONFIG_ERRORS);
			return;
		}
		
		Arrays.stream(files)
				.map(YamlConfiguration::loadConfiguration)
				.forEach(this::loadConfig);
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
