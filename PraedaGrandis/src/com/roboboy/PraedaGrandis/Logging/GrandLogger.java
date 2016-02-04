package com.roboboy.PraedaGrandis.Logging;

import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Configuration.ConfigManager;

public class GrandLogger
{
	/**
	 * Sends a message to the log if the given LogType is enabled
	 * @param message Message to log
	 * @param type LogType of message
	 */
	public static void log(String message, LogType type) {
		if (!ConfigManager.getInstance().getEnabledLogTypes().contains(type)) return;
		PraedaGrandis.plugin.getLogger().log(type.getLevel(), type.getPrefix() + " " + message);
	}
}
