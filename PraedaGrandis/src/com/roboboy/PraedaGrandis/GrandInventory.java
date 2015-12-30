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
		public final UUID id;
		public final GrandItem grandItem;
		public final ItemSlotType slotType;
		
		private InventoryElement(ItemStack item, UUID id, GrandItem grandItem, ItemSlotType slotType) {
			this.item = item;
			this.id = id;
			this.grandItem = grandItem;
			this.slotType = slotType;
		}
	}
	
	private Map<UUID, InventoryElement> itemMap = new HashMap<>();
	private Map<String, Map<UUID, InventoryElement>> grandItemMap = new HashMap<>();
	
	/**
	 * Records an item as existing in a given slot type in the represented inventory.<br>
	 * <br>
	 * The item must represent a valid grand item, and contain a UUID in the metadata.
	 * @param item Item to add
	 * @param grandItem Represented GrandItem
	 * @param slotType Most unique SlotType to add to
	 */
	public void putItem(ItemStack item, GrandItem grandItem, ItemSlotType slotType)
	{
		//Construct new element
		InventoryElement element = new InventoryElement(item, getItemUUID(item), grandItem, slotType);
		
		//Put element on the itemMap
		itemMap.put(element.id, element);
		
		//Get corresponding slotTypeMap for the given grandItem, initializing if null
		Map<UUID, InventoryElement> items = grandItemMap.get(grandItem.getName());
		if (items == null) {
			items = new HashMap<>(4);
			grandItemMap.put(grandItem.getName(), items);
		}
		items.put(element.id, element);
	}
	
	/**
	 * Stops recording an item as existing in the represented inventory.<br>
	 * <br>
	 * The item must represent a valid grand item, and contain a UUID in the metadata.
	 * @param item Item to remove
	 * @param grandItem Represented GrandItem
	 */
	public void removeItem(ItemStack item, GrandItem grandItem) {
		//Get and remove element from the itemMap
		InventoryElement oldElement = itemMap.remove(getItemUUID(item));
		
		//Decrement value at old slot type
		if (oldElement != null) {
			Map<UUID, InventoryElement> items = grandItemMap.get(grandItem.getName());
			items.remove(oldElement.id);
		}
	}
	
	/**
	 * Returns a list of all elements in this inventory
	 * @return A list of all elements
	 */
	public List<InventoryElement> getItems() {
		return new ArrayList<>(itemMap.values());
	}
	
	public List<InventoryElement> getItems(String grandItemName) {
		Map<UUID, InventoryElement> items = grandItemMap.get(grandItemName);
		if (items == null) return Arrays.asList();
		return new ArrayList<>(itemMap.values());
	}
	
	public boolean containsGrandItem(String grandItemName) {
		return grandItemMap.containsKey(grandItemName);
	}
	
	private UUID getItemUUID(ItemStack item) {
		return UUID.fromString(NBTStorage.newTarget(item, PraedaGrandis.STORAGE_ITEM_ID).getString(""));
	}
}
