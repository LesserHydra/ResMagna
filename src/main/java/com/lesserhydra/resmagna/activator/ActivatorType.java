package com.lesserhydra.resmagna.activator;

import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;

/**
 * Activators
 */
public enum ActivatorType {
	
/*	Type			Parent					Explanation								Target				*/
/*------------------------------------------------------------------------------------------------------*/
	NONE,									//Invalid type, or no parent			NA
	
	TIMER,									//Special case; not event driven		NA
	
	THROW,									//Egg, snowball, enderpearl, ect.		NA
	DROP,									//Pressed Q								NA
	
	EQUIP,									//Item moved into specified slotType	NA
	UNEQUIP,								//Item moved from specified slotType	NA
	
	MOVE,									//Holder location changed				From location
	MOVEWALK		(MOVE),					//Holder walking						.
	MOVEUP			(MOVE),					//Holder moved upwards					.
	MOVEDOWN		(MOVE),					//Holder moved downwards				.
	
	TELEPORT		(MOVE),					//Holder teleported						From location
	PORTAL			(TELEPORT),				//Holder about to go through portal		.
	
	LOOK,									//Holder direction changed				From location (direction)
	
	CLICK,									//Holder clicked						What was clicked
	CLICKLEFT		(CLICK),				//Holder left-clicked					NA
	CLICKRIGHT		(CLICK),				//Holder right-clicked					NA
	INTERACT		(CLICKRIGHT),			//Holder right-clicked on an entity		What was clicked
	INTERACTPLAYER	(INTERACT),				//Holder right-clicked on a player		.
	INTERACTMOB		(INTERACT),				//Holder right-clicked on a mob			.
	
	BLOCKBREAK,								//Holder breaks a block					Location of block broken
	
	BREAK,									//Item breaks							NA
	
	ATTACK,									//Holder attacked something				What was attacked
	ATTACKPLAYER	(ATTACK),				//Holder attacked player				.
	ATTACKSELF		(ATTACKPLAYER),			//Holder attacked self					.
	ATTACKMOB		(ATTACK),				//Holder attacked mob					.
	
	HURT,									//Holder was hurt by something			What owner was hurt by
	HURTPLAYER		(HURT),					//Holder was hurt by player				.
	HURTSELF		(HURTPLAYER),			//Holder was hurt by self				.
	HURTMOB			(HURT),					//Holder was hurt by mob				.
	HURTOTHER		(HURT),					//Holder was hurt by environmental		NA
	
	KILL,									//Holder killed something				What was killed
	KILLPLAYER		(KILL),					//Holder killed player					.
	KILLSELF		(KILLPLAYER),			//Holder killed self					.
	KILLMOB			(KILL),					//Holder killed mob						.
	
	DEATH,									//Holder was killed						What owner was killed by
	DEATHPLAYER		(DEATH),				//Holder was killed by player			.
	DEATHSELF		(DEATHPLAYER),			//Holder was killed by self				.
	DEATHMOB		(DEATH),				//Holder was killed by mob				.
	DEATHOTHER		(DEATH);				//Holder was killed by environmental	NA

	private final ActivatorType parent;
	
	private ActivatorType() {
		this.parent = null;
	}
	
	private ActivatorType(ActivatorType parent) {
		this.parent = parent;
	}
	
	/**
	 * Checks for supertype equivilence. For example, ATTACKSELF is a subtype of ATTACKPLAYER,
	 * which is a subtype of ATTACK.
	 * @param supertype 
	 * @return True if this type is a subtype of given type, false otherwise
	 */
	public boolean isSubtypeOf(ActivatorType supertype) {
		//Is subtype of self
		if (this == supertype) return true;
		
		//No relation
		if (parent == null) return false;
		
		//Is subtype of anything parent is subtype of
		return parent.isSubtypeOf(supertype);
	}

	public boolean isNull() {
		return (this == NONE);
	}
	
	/**
	 * Find ActivatorType by name
	 * @param activatorName Name of required type
	 * @return The matching type, or NONE if not found
	 */
	public static ActivatorType fromName(String activatorName){
		//Find by name
		activatorName = activatorName.toUpperCase();
		for (ActivatorType type : values()) {
			if (activatorName.equals(type.name())) return type;
		}
		
		//Invalid name
		GrandLogger.log("Invalid activator: on" + activatorName, LogType.CONFIG_ERRORS);
		return NONE;
	}
	
}
