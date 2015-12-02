package com.roboboy.PraedaGrandis.Abilities;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum ItemSlotType
{
/*	Type		Parent			Explanation								*/
/*----------------------------------------------------------------------*/
	NONE		(null) {		//Nowhere
		@Override public List<ItemStack> getItems(Player p) {return Arrays.asList();}
	},
	
	ANY			(NONE) {		//Anywhere on the player
		@Override public List<ItemStack> getItems(Player p) {
			return Arrays.asList( ArrayUtils.addAll(p.getInventory().getContents(), p.getInventory().getArmorContents()) );
		}
	},
	
	STORED		(ANY) {			//Bulk inventory (not hotbar or armor)
		@Override public List<ItemStack> getItems(Player p) {
			return Arrays.asList(p.getInventory().getContents());
		}
	},
	
	WORN		(ANY) {			//Any armor slot
		@Override public List<ItemStack> getItems(Player p) {
			return Arrays.asList(p.getInventory().getArmorContents());
		}
	},
	
	HELMET		(WORN) {		//Helmet slot
		@Override public List<ItemStack> getItems(Player p) {
			return Arrays.asList(p.getInventory().getHelmet());
		}
	},
	
	CHESTPLATE	(WORN) {		//Chestplate slot
		@Override public List<ItemStack> getItems(Player p) {
			return Arrays.asList(p.getInventory().getChestplate());
		}
	},
	
	LEGGINGS	(WORN) {		//Leggings slot
		@Override public List<ItemStack> getItems(Player p) {
			return Arrays.asList(p.getInventory().getLeggings());
		}
	},
	
	BOOTS		(WORN) {		//Boots slot
		@Override public List<ItemStack> getItems(Player p) {
			return Arrays.asList(p.getInventory().getBoots());
		}
	},
	
	HOTBAR		(ANY) {			//Anywhere on the hotbar
		@Override public List<ItemStack> getItems(Player p) {
			return Arrays.asList( Arrays.copyOf(p.getInventory().getContents(), 9) );
		}
	},
	
	HELD		(HOTBAR) {		//Held in hand
		@Override public List<ItemStack> getItems(Player p) {
			return Arrays.asList(p.getItemInHand());
		}
	};
	
	private final ItemSlotType parent;
	
	private ItemSlotType(ItemSlotType parent) {
		this.parent = parent;
	}
	
	/**
	 * Gets all items that match this slot type in a given player's inventory
	 * @param p The player to get from
	 * @return A fixed-size list with the resulting ItemStacks
	 */
	public abstract List<ItemStack> getItems(Player p);
	
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
		default: throw new IllegalArgumentException("Invalid armor slot (0-3): " + i);
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
