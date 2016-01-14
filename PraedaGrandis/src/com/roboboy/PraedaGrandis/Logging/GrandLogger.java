package com.roboboy.PraedaGrandis.Logging;

import com.roboboy.PraedaGrandis.PraedaGrandis;

public class GrandLogger
{
	private final PraedaGrandis plugin;
	
	public GrandLogger(PraedaGrandis plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Sends a message to the log if the given LogType is enabled
	 * @param message Message to log
	 * @param type LogType of message
	 */
	public void log(String message, LogType type) {
		if (!plugin.configManager.getEnabledLogTypes().contains(type)) return;
		plugin.getLogger().log(type.getLevel(), type.getPrefix() + " " + message);
	}
}
