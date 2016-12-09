package com.lesserhydra.resmagna.activator;

import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;

import java.util.Arrays;

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
	MOVE_WALK       (MOVE),					//Holder walking						.
	MOVE_UP         (MOVE),					//Holder moved upwards					.
	MOVE_DOWN       (MOVE),					//Holder moved downwards				.
	
	TELEPORT		(MOVE),					//Holder teleported						From location
	PORTAL			(TELEPORT),				//Holder about to go through portal		.
	
	LOOK,									//Holder direction changed				From location (direction)
	
	CLICK,									//Holder clicked						NA
	LEFT_CLICK          (CLICK),			//Holder left-clicked					NA
	RIGHT_CLICK         (CLICK),			//Holder right-clicked					NA
	LEFT_CLICK_BLOCK    (LEFT_CLICK),		//Holder left-clicked					Block that was clicked
	RIGHT_CLICK_BLOCK   (RIGHT_CLICK),      //Holder right-clicked					Block that was clicked
	INTERACT		    (RIGHT_CLICK),		//Holder right-clicked on an entity		Entity that was clicked
	
	BREAK_BLOCK,							//Holder breaks a block					Location of block broken
	
	BREAK,									//Item breaks							NA
	
	ATTACK,									//Holder attacked something				What was attacked
	HURT,									//Holder was hurt by something			What owner was hurt by
	KILL,									//Holder killed something				What was killed
	DEATH;									//Holder was killed						What owner was killed by
	
	private final ActivatorType parent;
	ActivatorType() { this.parent = null; }
	ActivatorType(ActivatorType parent) { this.parent = parent; }
	
	/**
	 * Checks for supertype equivalence. For example, PORTAL is a subtype of TELEPORT,
	 * which is a subtype of MOVE.
	 * @param supertype Supertype
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
	
	/**
	 * Checks if this type is valid.
	 * @return True if this type is valid
	 */
	public boolean isValid() { return this != NONE; }
	
	/**
	 * Find ActivatorType by name
	 * @param activatorName Name of required type
	 * @return The matching type, or NONE if not found
	 */
	public static ActivatorType fromName(String activatorName){
		//Find by name
		String lookup = activatorName.toUpperCase().replace("_", "");
		for (ActivatorType type : values()) {
			if (lookup.equals(type.name().replace("_", ""))) return type;
		}
		
		//Invalid name
		GrandLogger.log("Invalid activator: on" + activatorName, LogType.CONFIG_ERRORS);
		return NONE;
	}
	
}
