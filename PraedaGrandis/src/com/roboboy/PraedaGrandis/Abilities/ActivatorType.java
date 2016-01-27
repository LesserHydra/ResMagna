package com.roboboy.PraedaGrandis.Abilities;

/**
 * Activators
 */
public enum ActivatorType
{
/*	Type			Parent					Explanation								Target				*/
/*------------------------------------------------------------------------------------------------------*/
	NONE			(null),					//Invalid type, or no parent			NA
	
	TIMER			(NONE),					//Special case; not event driven		NA
	
	THROW			(NONE),					//Egg, snowball, enderpearl, ect.		NA
	DROP			(NONE),					//Pressed Q								NA
	
	EQUIP			(NONE),					//Item moved into specified slotType	NA
	UNEQUIP			(NONE),					//Item moved from specified slotType	NA
	
	MOVE			(NONE),					//Owner location changed
	MOVEWALK		(MOVE),					//Owner walking
	MOVEUP			(MOVE),
	MOVEDOWN		(MOVE),
	
	TELEPORT		(MOVE),					//Owner teleported						NA
	PORTAL			(TELEPORT),				//Owner about to go through portal		NA
	
	LOOK			(NONE),					//Owner direction changed				NA
	
	CLICK			(NONE),					//Owner clicked							What was clicked
	CLICKLEFT		(CLICK),				//Owner left-clicked					NA
	CLICKRIGHT		(CLICK),				//Owner right-clicked					NA
	INTERACT		(CLICKRIGHT),			//Owner right-clicked on an entity		What was clicked
	INTERACTPLAYER	(INTERACT),				//Owner right-clicked on a player		.
	INTERACTMOB		(INTERACT),				//Owner right-clicked on a mob			.
	
	BREAK			(NONE),					//Item breaks							NA
	
	ATTACK			(NONE),					//Owner attacked something				What was attacked
	ATTACKPLAYER	(ATTACK),				//Owner attacked player					.
	ATTACKSELF		(ATTACKPLAYER),			//Owner attacked self					.
	ATTACKMOB		(ATTACK),				//Owner attacked mob					.
	
	HURT			(NONE),					//Owner was hurt by something			What owner was hurt by
	HURTPLAYER		(HURT),					//Owner was hurt by player				.
	HURTSELF		(HURTPLAYER),			//Owner was hurt by self				.
	HURTMOB			(HURT),					//Owner was hurt by mob					.
	HURTOTHER		(HURT),					//Owner was hurt by environmental		NA
	
	KILL			(NONE),					//Owner killed something				What was killed
	KILLPLAYER		(KILL),					//Owner killed player					.
	KILLSELF		(KILLPLAYER),			//Owner killed self						.
	KILLMOB			(KILL),					//Owner killed mob						.
	
	DEATH			(NONE),					//Owner was killed						What owner was killed by
	DEATHPLAYER		(DEATH),				//Owner was killed by player			.
	DEATHSELF		(DEATHPLAYER),			//Owner was killed by self				.
	DEATHMOB		(DEATH),				//Owner was killed by mob				.
	DEATHOTHER		(DEATH);				//Owner was killed by environmental		NA

	private final ActivatorType parent;
	
	private ActivatorType(ActivatorType parent) {
		this.parent = parent;
	}
	
	/**
	 * Checks for supertype equivilence. For example, ATTACKSELF is a subtype of ATTACKPLAYER,
	 * which is a subtype of ATTACK.
	 * @param supertype 
	 * @return True if this type is a subtype of given type, false otherwise
	 */
	public boolean isSubtypeOf(ActivatorType supertype)
	{
		if (this == NONE) return false;			//Base case
		
		if (this == supertype) return true; 	//Is subtype of self
		return parent.isSubtypeOf(supertype);	//Is subtype of anything parent is subtype of, ect
	}

	public boolean isNull() {
		return (this == NONE);
	}
}
