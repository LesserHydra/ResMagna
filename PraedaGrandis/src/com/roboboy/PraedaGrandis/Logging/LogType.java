package com.roboboy.PraedaGrandis.Logging;

import java.util.logging.Level;

public enum LogType
{
	TOO_MUCH_INFO	("[TMI]", Level.INFO),
	CONFIG_PARSING	("[CFG]", Level.INFO),
	CONFIG_ERRORS	("[ERR]", Level.WARNING),
	ACTIVATORS		("[ACT]", Level.INFO),
	DEBUG			("[DBG]", Level.INFO);

	private final String	typeString;
	private final Level		level;
	
	private LogType(String typeString, Level level) {
		this.typeString = typeString;
		this.level = level;
	}
	
	/**
	 * Returns the logging level for this type, ex: Level.WARNING
	 * @return 
	 */
	public Level getLevel() {
		return level;
	}
	
	/**
	 * Returns a message prefix for this type, ex: [CFG]
	 * @return Message prefix for this type
	 */
	@Override
	public String toString() {
		return typeString;
	}
}
