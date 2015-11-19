package com.roboboy.PraedaGrandis.Abilities;

public enum ItemSlotType
{
/*	Type		Parent			Explanation								*/
/*----------------------------------------------------------------------*/
	NONE		(null),			//Nowhere
	ANY			(NONE),			//Anywhere on the player
	
	STORED		(ANY),			//Bulk inventory (not hotbar or armor)
	
	WORN		(ANY),			//Any armor slot
	HELMET		(WORN),			//Helmet slot
	CHESTPLATE	(WORN),			//Chestplate slot
	LEGGINGS	(WORN),			//Leggings slot
	BOOTS		(WORN),			//Boots slot
	
	HOTBAR		(ANY),			//Anywhere on the hotbar
	HELD		(HOTBAR);		//Held in hand
	
	private final ItemSlotType parent;
	
	private ItemSlotType(ItemSlotType parent) {
		this.parent = parent;
	}
	
	/**
	 * Checks for supertype equivilence. For example, HELD is a subtype of HOTBAR,
	 * which is a subtype of ANY.
	 * @param supertype 
	 * @return True if this type is a subtype of given type, false otherwise
	 */
	public boolean isSubtypeOf(ItemSlotType supertype)
	{
		if (this == NONE) return false;			//Base case
		
		if (this == supertype) return true; 	//Is subtype of self
		if (parent == supertype) return true;	//Is subtype of parent
		return parent.isSubtypeOf(supertype);	//Is subtype of parent's parent, ect
	}
	
	public static ItemSlotType getArmorSlotType(int i)
	{
		switch (i) {
		case 0: return HELMET;
		case 1: return CHESTPLATE;
		case 2: return LEGGINGS;
		case 3: return BOOTS;
		default: return NONE;
		}
	}
	
	/*public static boolean matches(ItemSlotType toMatch, int slot, Player holder)
	{
		if (toMatch == HELD) return (slot == holder.getInventory().getHeldItemSlot());
		if (toMatch == WORN) return (slot < 0);
		if (toMatch == HOTBAR) return (slot >= 0 && slot < 9);
		if (toMatch == STORED) return (slot >= 9);
		
		return true;
	}*/
}
