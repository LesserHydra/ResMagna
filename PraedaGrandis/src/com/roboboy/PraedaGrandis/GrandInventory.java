package com.roboboy.PraedaGrandis;

import com.comphenix.attribute.NBTStorage;
import com.roboboy.PraedaGrandis.Abilities.*;
import com.roboboy.PraedaGrandis.Configuration.*;
import java.util.*;
import org.bukkit.inventory.ItemStack;

public class GrandInventory
{
	/**
	 * Represents an item in inventory along with its pre-determined GrandItem and the ItemSlotType it exists in.<br>
	 */
	public class InventoryElement
	{
		public final ItemStack item;
		public final GrandItem grandItem;
		public final ItemSlotType slotType;
		
		private InventoryElement(ItemStack item, GrandItem grandItem, ItemSlotType slotType) {
			this.item = item;
			this.grandItem = grandItem;
			this.slotType = slotType;
		}
	}
	
	private Map<UUID, InventoryElement> itemMap = new HashMap<>();
	
	/**
	 * Records an item as existing in a given slot type in the represented inventory.<br>
	 * <br>
	 * The item must represent a valid grand item, and contain a UUID in the metadata.
	 * @param item Item to add
	 * @param grandItem Represented GrandItem
	 * @param slotType Most unique SlotType to add to
	 */
	public void putItem(ItemStack item, GrandItem grandItem, ItemSlotType slotType) {
		InventoryElement element = new InventoryElement(item, grandItem, slotType);
		itemMap.put(getItemUUID(item), element);
	}
	
	/**
	 * Stops recording an item as existing in the represented inventory.<br>
	 * <br>
	 * The item must represent a valid grand item, and contain a UUID in the metadata.
	 * @param item Item to remove
	 * @param grandItem Represented GrandItem
	 */
	public void removeItem(ItemStack item) {
		itemMap.remove(getItemUUID(item));
	}
	
	/**
	 * Returns a list of all elements in this inventory
	 * @return A list of all elements
	 */
	public List<InventoryElement> getItems() {
		return new ArrayList<>(itemMap.values());
	}
	
	private UUID getItemUUID(ItemStack item) {
		return UUID.fromString(NBTStorage.newTarget(item, PraedaGrandis.STORAGE_ITEM_ID).getString(""));
	}
}
