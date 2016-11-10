package com.roboboy.PraedaGrandis.Arguments;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;

public enum ItemSlotType
{
/*	Type		Parent			Explanation								*/
/*----------------------------------------------------------------------*/
	NONE		(null) {		//Nowhere
		@Override public List<ItemStack> getItems(Player p) { return Collections.emptyList(); }
	},
	
	ANY			(NONE) {		//Anywhere on the player
		@Override public List<ItemStack> getItems(Player p) {
			PlayerInventory inv = p.getInventory();
			return Arrays.asList(inv.getContents());
		}
	},
	
	WORN		(ANY) {			//Any armor slot
		@Override public List<ItemStack> getItems(Player p) {
			return Arrays.asList(p.getInventory().getArmorContents());
		}
	},
	
	HELMET		(WORN) {		//Helmet slot
		@Override public List<ItemStack> getItems(Player p) {
			return Collections.singletonList(p.getInventory().getHelmet());
		}
	},
	
	CHESTPLATE	(WORN) {		//Chestplate slot
		@Override public List<ItemStack> getItems(Player p) {
			return Collections.singletonList(p.getInventory().getChestplate());
		}
	},
	
	LEGGINGS	(WORN) {		//Leggings slot
		@Override public List<ItemStack> getItems(Player p) {
			return Collections.singletonList(p.getInventory().getLeggings());
		}
	},
	
	BOOTS		(WORN) {		//Boots slot
		@Override public List<ItemStack> getItems(Player p) {
			return Collections.singletonList(p.getInventory().getBoots());
		}
	},
	
	STORED		(ANY) {			//Bulk inventory (not hotbar or armor)
		@Override public List<ItemStack> getItems(Player p) {
			ItemStack[] invContents = p.getInventory().getStorageContents();
			return Arrays.asList( Arrays.copyOfRange(invContents, 9, invContents.length) );
		}
	},
	
	HOTBAR		(ANY) {			//Anywhere on the hotbar, or off hand
		@Override public List<ItemStack> getItems(Player p) {
			PlayerInventory inv = p.getInventory();
			return Arrays.asList( Arrays.copyOf(inv.getContents(), 10) );
		}
	},
	
	HELD		(HOTBAR) {		//Held in hand
		@Override public List<ItemStack> getItems(Player p) {
			PlayerInventory inv = p.getInventory();
			return Arrays.asList(inv.getItemInMainHand(), inv.getItemInOffHand());
		}
	},
	
	HELDMAIN	(HELD) {		//Held in main hand
		@Override public List<ItemStack> getItems(Player p) {
			return Collections.singletonList(p.getInventory().getItemInMainHand());
		}
	},
	
	HELDOFF		(HELD) {		//Held in off hand
		@Override public List<ItemStack> getItems(Player p) {
			return Collections.singletonList(p.getInventory().getItemInOffHand());
		}
	},
	
	UNHELD		(HOTBAR) {		//In the hotbar, but not held in hand
		@Override public List<ItemStack> getItems(Player p) {
			PlayerInventory inv = p.getInventory();
			/*ItemStack[] invArray = p.getInventory().getContents();
			ItemStack[] resultArray = new ItemStack[8];
			int heldSlot = inv.getHeldItemSlot();
			for (int i = 0, j = 0; i < 8; ++i, ++j) {
				if (j == heldSlot) ++j;
				resultArray[i] = invArray[j];
			}
			return Arrays.asList(resultArray);*/
			return Arrays.asList( ArrayUtils.remove(Arrays.copyOf(inv.getContents(), 9), inv.getHeldItemSlot()) );
		}
	};
	
	private static final Set<ItemSlotType> unique;
	
	private final ItemSlotType parent;
	private EnumSet<ItemSlotType> children;
	
	static {
		Set<ItemSlotType> workingUnique = EnumSet.allOf(ItemSlotType.class);
		for (ItemSlotType type : values()) {
			if (type.isNull()) continue;
			type.children = EnumSet.noneOf(ItemSlotType.class);
			type.parent.registerSubtype(type);
			workingUnique.remove(type.parent);
		}
		unique = Collections.unmodifiableSet(workingUnique);
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
	
	public boolean isNull() { return this == NONE; }
	
	public boolean isUnique() { return this.children.isEmpty(); }
	
	private void registerSubtype(ItemSlotType subtype) {
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
	
	public static Set<ItemSlotType> getUniqueTypes() {
		return unique;
	}
	
	/**
	 * Find ItemSlotType by name
	 * @param typeName Name of required type, non null
	 * @return The matching type, or NONE if not found
	 */
	public static ItemSlotType fromName(String typeName){
		//Find by name
		typeName = typeName.toUpperCase();
		for (ItemSlotType type : values()) {
			if (typeName.equals(type.name())) return type;
		}
		
		//Invalid name
		GrandLogger.log("Invalid slot type: " + typeName, LogType.CONFIG_ERRORS);
		return NONE;
	}
	
}
