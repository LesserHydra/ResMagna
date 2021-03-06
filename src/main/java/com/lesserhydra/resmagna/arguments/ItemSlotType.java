package com.lesserhydra.resmagna.arguments;

import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum ItemSlotType {
	
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
	 * Checks for supertype equivalence. For example, HELD is a subtype of HOTBAR,
	 * which is a subtype of ANY.
	 * @param supertype 
	 * @return True if this type is a subtype of given type, false otherwise
	 */
	public boolean isSubtypeOf(ItemSlotType supertype) {
		return !isNull() && (this == supertype || parent.isSubtypeOf(supertype));
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
	
	public static ItemSlotType find(Player player, ItemStack item) {
		return fromTotalIndex(getIndex(player, item), player.getInventory().getHeldItemSlot());
	}
	
	public static Set<ItemSlotType> getUniqueTypes() { return unique; }
	
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
	
	private static int getIndex(Player player, ItemStack item) {
		ItemStack[] contents = player.getInventory().getContents();
		for (int i = 0; i < contents.length; ++i) {
			if (item.isSimilar(contents[i])) return i;
		}
		return -1;
	}
	
	public static ItemSlotType fromCreativeIndex(int i, int heldSlot) {
		//Negative values represent unfound
		if (i < 0) return NONE;
		//36-40 & 0-3 Hotbar
		if (i >= 36 && i <= 40) return (i - 36 == heldSlot ? HELDMAIN : UNHELD);
		if (i <= 3) return (i + 5 == heldSlot ? HELDMAIN : UNHELD);
		//4 Offhand
		if (i == 4) return HELDOFF;
		//5-8 Armor
		if (i == 5) return HELMET;
		if (i == 6) return CHESTPLATE;
		if (i == 7) return LEGGINGS;
		if (i == 8) return BOOTS;
		//9-35 Stored
		if (i <= 35) return STORED;

		return NONE;
	}
	
	public static ItemSlotType fromTotalIndex(int i, int heldSlot) {
		//Negative values represent unfound
		if (i < 0) return NONE;
		//Selected hotbar slot
		if (i == heldSlot) return HELDMAIN;
		//0-8 Hotbar
		if (i < 9) return UNHELD;
		//9-35 Stored
		if (i < 36) return STORED;
		//36-39 Armor
		if (i == 36) return BOOTS;
		if (i == 37) return LEGGINGS;
		if (i == 38) return CHESTPLATE;
		if (i == 39) return HELMET;
		//40 Offhand
		if (i == 40) return HELDOFF;
		
		return NONE;
	}
	
}
