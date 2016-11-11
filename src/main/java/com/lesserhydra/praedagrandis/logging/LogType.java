package com.lesserhydra.praedagrandis.logging;

import java.util.logging.Level;

public enum LogType
{
	TOO_MUCH_INFO	("[TMI]", Level.INFO),
	CONFIG_PARSING	("[CFG]", Level.INFO),
	CONFIG_ERRORS	("[ERR]", Level.WARNING),
	ACTIVATORS		("[ACT]", Level.INFO),
	DEBUG			("[DBG]", Level.INFO);

	private final String	prefix;
	private final Level		level;
	
	private LogType(String prefix, Level level) {
		this.prefix = prefix;
		this.level = level;
	}
	
	/**
	 * Returns a message prefix for this type, ex: [CFG]
	 * @return Message prefix for this type
	 */
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Returns the logging level for this type, ex: Level.WARNING
	 * @return 
	 */
	public Level getLevel() {
		return level;
	}
}
