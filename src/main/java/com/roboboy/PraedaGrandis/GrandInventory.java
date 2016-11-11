package com.roboboy.PraedaGrandis;

import com.comphenix.attribute.NBTStorage;
import com.roboboy.PraedaGrandis.Arguments.ItemSlotType;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Activator.ActivatorType;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;
import com.roboboy.PraedaGrandis.Configuration.ItemHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
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
	
	private final Player holderPlayer;
	private final Map<UUID, InventoryElement> itemMap = new HashMap<>();
	private final Map<String, Map<UUID, InventoryElement>> grandItemMap = new HashMap<>();
	
	public GrandInventory(Player holderPlayer) {
		this.holderPlayer = holderPlayer;
	}
	
	public void resetToPlayer() {
		//Clear maps
		itemMap.clear();
		grandItemMap.clear();
		
		//Read all items from player's inventory
		for (ItemSlotType slotType : ItemSlotType.getUniqueTypes()) {
			for (ItemStack item : slotType.getItems(holderPlayer)) {
				GrandItem gItem = ItemHandler.getInstance().matchItem(item);
				if (gItem != null) putItem(item, gItem, slotType);
			}
		}
	}
	
	/**
	 * Records an item as existing in a given slot type in the represented inventory.<br>
	 * <br>
	 * The item must represent a valid grand item, and contain a UUID in the metadata.
	 * @param item Item to add
	 * @param grandItem Represented GrandItem
	 * @param slotType Most unique SlotType to add to
	 */
	void putItem(ItemStack item, GrandItem grandItem, ItemSlotType slotType) {
		//Redirect to remove if slot type is none
		if (slotType == ItemSlotType.NONE) {
			removeItem(item);
			return;
		}
		
		//DEBUG
		if (grandItem.getName().equalsIgnoreCase("InvDebug")) {
			GrandLogger.log("Debug item added to " + slotType, LogType.DEBUG);
		}
		
		//Construct new element
		InventoryElement element = new InventoryElement(item, getItemUUID(item), grandItem, slotType);
		
		//Put element on the itemMap
		InventoryElement oldElement = itemMap.put(element.id, element);
		
		//Send unequip activator
		if (oldElement != null) {
			//DEBUG
			if (grandItem.getName().equalsIgnoreCase("InvDebug")) {
				GrandLogger.log("  Previous: " + oldElement.slotType, LogType.DEBUG);
			}
			oldElement.grandItem.activateAbilities(ActivatorType.UNEQUIP, oldElement.slotType, Target.makeEmpty(holderPlayer));
		}
		
		//Send equip activator
		grandItem.activateAbilities(ActivatorType.EQUIP, slotType, Target.makeEmpty(holderPlayer));
		
		//Get corresponding slotTypeMap for the given grandItem, initializing if null
		Map<UUID, InventoryElement> items = grandItemMap.get(element.grandItem.getName());
		if (items == null) {
			items = new HashMap<>(4);
			grandItemMap.put(element.grandItem.getName(), items);
		}
		items.put(element.id, element);
	}
	
	/**
	 * Stops recording an item as existing in the represented inventory.<br>
	 * <br>
	 * The item must represent a valid grand item, and contain a UUID in the metadata.
	 * @param item Item to remove
	 */
	void removeItem(ItemStack item) {
		//Get and remove element from the itemMap
		InventoryElement oldElement = itemMap.remove(getItemUUID(item));
		
		if (oldElement != null) {
			//DEBUG
			if (oldElement.grandItem.getName().equalsIgnoreCase("InvDebug")) {
				GrandLogger.log("Debug item removed from " + oldElement.slotType, LogType.DEBUG);
			}
			
			//Send unequip activator
			oldElement.grandItem.activateAbilities(ActivatorType.UNEQUIP, oldElement.slotType, Target.makeEmpty(holderPlayer));
			
			Map<UUID, InventoryElement> items = grandItemMap.get(oldElement.grandItem.getName());
			oldElement = items.remove(oldElement.id);
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
		if (items == null) return Collections.emptyList();
		return new ArrayList<>(items.values());
	}
	
	public boolean containsGrandItem(String grandItemName) {
		return grandItemMap.containsKey(grandItemName);
	}
	
	private UUID getItemUUID(ItemStack item) {
		return UUID.fromString(NBTStorage.newTarget(item, PraedaGrandis.STORAGE_ITEM_ID).getString(""));
	}
}
