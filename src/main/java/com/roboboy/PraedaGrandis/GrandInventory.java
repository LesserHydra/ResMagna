package com.roboboy.PraedaGrandis;

import com.comphenix.attribute.NBTStorage;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargetNone;
import com.roboboy.PraedaGrandis.Activator.ActivatorType;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;
import com.roboboy.PraedaGrandis.Configuration.ItemHandler;
import java.util.ArrayList;
import java.util.Collection;
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
	public void putItem(ItemStack item, GrandItem grandItem, ItemSlotType slotType) {		
		//Construct new element
		InventoryElement element = new InventoryElement(item, getItemUUID(item), grandItem, slotType);
		
		//Put element on the itemMap
		InventoryElement oldElement = itemMap.put(element.id, element);
		
		//Send unequip activator
		if (oldElement != null) {
			oldElement.grandItem.activateAbilities(ActivatorType.UNEQUIP, oldElement.slotType, new Target(new TargetNone(), holderPlayer, new TargetNone()));
		}
		
		//Send equip activator
		grandItem.activateAbilities(ActivatorType.EQUIP, slotType, new Target(new TargetNone(), holderPlayer, new TargetNone()));
		
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
	public void removeItem(ItemStack item) {
		//Get and remove element from the itemMap
		InventoryElement oldElement = itemMap.remove(getItemUUID(item));
		
		if (oldElement != null) {
			//Send unequip activator
			oldElement.grandItem.activateAbilities(ActivatorType.UNEQUIP, oldElement.slotType, new Target(new TargetNone(), holderPlayer, new TargetNone()));
			
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
	
	public boolean containsGrandItem(String grandItemName, ItemSlotType itemSlotType) {
		return getItems(grandItemName).stream()
				.anyMatch(element -> element.slotType.isSubtypeOf(itemSlotType));
	}
	
	public boolean containsAll(Collection<String> grandItemNames, ItemSlotType itemSlotType) {
		return grandItemNames.stream()
				.allMatch(name -> containsGrandItem(name, itemSlotType));
	}
	
	private UUID getItemUUID(ItemStack item) {
		return UUID.fromString(NBTStorage.newTarget(item, PraedaGrandis.STORAGE_ITEM_ID).getString(""));
	}
	
	/*
	public Stream<InventoryElement> getItems(ItemSlotType slotType) {
		return slotType.getItems(holderPlayer).stream()
				.map(this::itemToElement)
				.filter(Objects::nonNull);
	}
	
	public Stream<InventoryElement> getItems(ItemSlotType slotType, String grandItemName) {
		return slotType.getItems(holderPlayer).stream()
				.map(this::itemToElement)
				.filter(element -> element.grandItem.getName().equalsIgnoreCase(grandItemName))
				.filter(Objects::nonNull);
	}
	
	public boolean contains(ItemSlotType slotType, String grandItemName) {
		return slotType.getItems(holderPlayer).stream()
				.map(item -> ItemHandler.getInstance().matchItem(item))
				.filter(Objects::nonNull)
				.anyMatch(grandItem -> grandItem.getName().equalsIgnoreCase(grandItemName));
	}
	
	public boolean containsAll(ItemSlotType slotType, Collection<String> names) {
		Set<String> toFind = new HashSet<>(names);
		slotType.getItems(holderPlayer).stream()
				.map(item -> ItemHandler.getInstance().matchItem(item))
				.filter(Objects::nonNull)
				.forEach(grandItem -> toFind.remove(grandItem.getName().toLowerCase()));
		return toFind.isEmpty();
	}
	
	private InventoryElement itemToElement(ItemStack item) {
		GrandItem grandItem = ItemHandler.getInstance().matchItem(item);
		if (grandItem == null) return null;
		return new InventoryElement(item, grandItem);
	}
	*/
}
