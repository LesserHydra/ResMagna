package com.roboboy.PraedaGrandis.Logging;

import java.util.EnumSet;
import com.roboboy.PraedaGrandis.PraedaGrandis;

public class GrandLogger
{
	private final PraedaGrandis plugin;
	private final EnumSet<LogType> enabledLogTypes;
	
	/**
	 * Constructs a GrandLogger with no LogTypes enabled
	 * @param plugin Plugin
	 */
	public GrandLogger(PraedaGrandis plugin) {
		this.plugin = plugin;
		this.enabledLogTypes = EnumSet.noneOf(LogType.class);
	}
	
	/**
	 * Constructs a GrandLogger with given LogTypes enabled
	 * @param plugin Plugin
	 * @param typesToLog LogTypes to enable
	 */
	public GrandLogger(PraedaGrandis plugin, EnumSet<LogType> typesToLog) {
		this.plugin = plugin;
		this.enabledLogTypes = typesToLog.clone();
	}
	
	/**
	 * Sends a message to the log if the given LogType is enabled
	 * @param message Message to log
	 * @param type LogType of message
	 */
	public void log(String message, LogType type) {
		if (!enabledLogTypes.contains(type)) return;
		plugin.getLogger().log(type.getLevel(), type.toString() + " " + message);
	}
}
