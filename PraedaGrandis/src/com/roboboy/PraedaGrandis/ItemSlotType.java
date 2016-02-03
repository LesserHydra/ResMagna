package com.roboboy.PraedaGrandis;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
	
	STORED		(ANY) {			//Bulk inventory (not hotbar or armor)
		@Override public List<ItemStack> getItems(Player p) {
			ItemStack[] invContents = p.getInventory().getContents();
			return Arrays.asList( Arrays.copyOfRange(invContents, 9, invContents.length) );
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
	},
	
	UNHELD		(HOTBAR) {		//Held in hand
		@Override public List<ItemStack> getItems(Player p) {
			PlayerInventory inv = p.getInventory();
			return Arrays.asList( ArrayUtils.remove(Arrays.copyOf(inv.getContents(), 9), inv.getHeldItemSlot()) );
		}
	};
	
	private final ItemSlotType parent;
	private EnumSet<ItemSlotType> children;
	
	static {
		for (ItemSlotType type : values()) {
			if (type.isNull()) continue;
			type.children = EnumSet.noneOf(ItemSlotType.class);
			type.parent.registerSubtype(type);
		}
	}
	
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
		if (isNull()) return false;				//Base case
		
		if (this == supertype) return true; 	//Is subtype of self
		//if (parent == supertype) return true;	//Is subtype of parent
		return parent.isSubtypeOf(supertype);	//Is subtype of parent, ect
	}
	
	public EnumSet<ItemSlotType> getSupertypes() {
		EnumSet<ItemSlotType> results = EnumSet.noneOf(ItemSlotType.class);
		
		ItemSlotType type = this;
		while (!type.isNull()) {
			results.add(type);
			type = type.parent;
		}
		
		return results;
	}
	
	public boolean isNull() {
		return (this == NONE);
	}
	
	private void registerSubtype(final ItemSlotType subtype) {
		if (isNull()) return;
		children.add(subtype);
		parent.registerSubtype(subtype);
	}
	
	public static ItemSlotType getSlotType(SlotType slotType, int slotNumber, int heldSlotNumber) {
		switch (slotType) {
		case CONTAINER:		return STORED;
		case ARMOR:			return getArmorSlotType(slotNumber);
		case QUICKBAR:		return getHotbarSlotType(slotNumber, heldSlotNumber);
		default:			return NONE;
		}
	}
	
	private static ItemSlotType getArmorSlotType(int i) {
		switch (i) {
		case 39: 	return HELMET;
		case 38:	return CHESTPLATE;
		case 37:	return LEGGINGS;
		case 36:	return BOOTS;
		
		//Should be impossible, unless inventory structure changes (aka Minecraft 1.9)
		default: throw new IllegalArgumentException("Invalid armor slot (36-39): " + i);
		}
	}
	
	public static ItemSlotType getHotbarSlotType(final int slotNumber, final int heldSlotNumber) {
		if (slotNumber == heldSlotNumber) return ItemSlotType.HELD;
		return ItemSlotType.UNHELD;
	}
	
	public static EnumSet<ItemSlotType> getUniqueTypes() {
		EnumSet<ItemSlotType> results = EnumSet.noneOf(ItemSlotType.class);
		for (ItemSlotType type : values()) {
			if (type.isNull()) continue;
			if (type.children.isEmpty()) results.add(type);
		}
		return results;
	}
}