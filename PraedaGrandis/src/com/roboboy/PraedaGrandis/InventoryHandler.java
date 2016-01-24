package com.roboboy.PraedaGrandis;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import com.roboboy.PraedaGrandis.Abilities.ItemSlotType;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;

public class InventoryHandler implements Listener
{
	private final PraedaGrandis plugin;
	private Map<String, GrandInventory> playerInventories = new HashMap<>();
	
	public InventoryHandler(PraedaGrandis plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Forgets about previously handled inventories, and treats all
	 * players as if they had just logged on.<br>
	 * <br>
	 * <strong>The ItemHandler must be reloaded first.</strong>
	 */
	public void reload() {
		playerInventories.clear();
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			registerPlayer(p);
		}
	}
	
	public GrandInventory getItemsFromPlayer(Player p) {
		return playerInventories.get(p.getName());
	}
	
	//Player logs in
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		registerPlayer(e.getPlayer());
	}
	
	//Player changes selected item
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChangeSelected(PlayerItemHeldEvent e) {
		GrandInventory gInv = playerInventories.get(e.getPlayer().getName());
		ItemStack[] playerItems = e.getPlayer().getInventory().getContents();
		
		ItemStack oldItem = playerItems[e.getPreviousSlot()];
		GrandItem oldGrandItem = plugin.itemHandler.matchItem(oldItem);
		if (oldGrandItem != null) {
			gInv.putItem(oldItem, oldGrandItem, ItemSlotType.UNHELD);
		}
		
		ItemStack newItem = playerItems[e.getNewSlot()];
		GrandItem newGrandItem = plugin.itemHandler.matchItem(newItem);
		if (newGrandItem != null) {
			gInv.putItem(newItem, newGrandItem, ItemSlotType.HELD);
		}
	}
	
	//Player 
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(InventoryClickEvent e)
	{
		if (!(e.getWhoClicked() instanceof Player)) return; //Is this even possible?
		
		Player p = (Player)e.getWhoClicked();
		if (e instanceof InventoryCreativeEvent) {
			resetInventoryNextTick(p);
			return;
		}
		
		if (e.getAction() == InventoryAction.NOTHING || e.getAction() == InventoryAction.CLONE_STACK || e.getAction() == InventoryAction.UNKNOWN) return;
		
		PlayerInventory inv = p.getInventory();
		GrandInventory gInv = playerInventories.get(p.getName());
		ItemStack currentItem = e.getCurrentItem();
		ItemStack cursorItem = e.getCursor();
		GrandItem currentGrandItem = plugin.itemHandler.matchItem(currentItem);
		GrandItem cursorGrandItem = plugin.itemHandler.matchItem(cursorItem);
		ItemSlotType clickedSlotType = ItemSlotType.getSlotType(e.getSlotType(), e.getSlot(), inv.getHeldItemSlot());
		
		//if (currentGrandItem == null && cursorGrandItem == null) return;
		
		p.sendMessage("------------------------------");
		p.sendMessage("  Action: " + e.getAction());
		if (currentGrandItem != null) p.sendMessage("  Current: " + currentGrandItem.getName());
		if (cursorGrandItem != null) p.sendMessage("  Cursor: " + cursorGrandItem.getName());
		p.sendMessage("  ClickedSlotType: " + clickedSlotType);
		p.sendMessage("  SlotNumber: " + e.getSlot());
		//p.sendMessage("  RawSlotNumber: " + e.getRawSlot());
		
		//Clicked on other inventory
		if (!inv.equals(e.getClickedInventory())) {
			//TODO: Figure out where the item will be moved to
			if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				p.sendMessage("  Item moved into inventory from other");
				resetInventoryNextTick(p);
			}
			return;
		}
		
		//Shift-clicked
		if (currentGrandItem != null && e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			InventoryType topInventoryType = e.getView().getTopInventory().getType();
			
			//Current item is shift-clicked to another place in inventory
			if (topInventoryType == InventoryType.CRAFTING || topInventoryType == InventoryType.WORKBENCH) {
				//TODO: Figure out where the item will be moved to
				p.sendMessage("  Current item is shift-clicked to another place in inventory");
				resetInventoryNextTick(p);
			}
			//Current item is shift-clicked to another inventory
			else if (topInventoryType == InventoryType.ANVIL || topInventoryType == InventoryType.CHEST
					|| topInventoryType == InventoryType.DISPENSER || topInventoryType == InventoryType.DROPPER
					|| topInventoryType == InventoryType.ENCHANTING || topInventoryType == InventoryType.ENDER_CHEST
					|| topInventoryType == InventoryType.HOPPER){
				//TODO: Check for failure (inventory is full)
				p.sendMessage("  Current item is shift-clicked to another inventory");
				gInv.removeItem(currentItem);
			}
			//TODO: Handle cases where only certain items are put into the other inventory
			
			return;
		}
		
		//Current item is removed from inventory
		if (currentGrandItem != null && (
				e.getAction() == InventoryAction.COLLECT_TO_CURSOR || e.getAction() == InventoryAction.DROP_ALL_SLOT
				|| e.getAction() == InventoryAction.DROP_ONE_SLOT || e.getAction() == InventoryAction.PICKUP_ALL
				|| e.getAction() == InventoryAction.PICKUP_HALF || e.getAction() == InventoryAction.PICKUP_ONE
				|| e.getAction() == InventoryAction.PICKUP_SOME || e.getAction() == InventoryAction.SWAP_WITH_CURSOR)
				) {
			p.sendMessage("  Current item is removed from inventory");
			gInv.removeItem(currentItem);
		}
		
		//Cursor item is added to inventory
		if (cursorGrandItem != null && (
				e.getAction() == InventoryAction.PLACE_ALL || e.getAction() == InventoryAction.PLACE_ONE
				|| e.getAction() == InventoryAction.PLACE_SOME || e.getAction() == InventoryAction.SWAP_WITH_CURSOR)) {
			p.sendMessage("  Cursor item is added to inventory");
			gInv.putItem(cursorItem, cursorGrandItem, clickedSlotType);
		}
		
		//Current item is swapped with hotbar item
		if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
			ItemStack hotbarItem = inv.getItem(e.getHotbarButton());
			GrandItem hotbarGrandItem = plugin.itemHandler.matchItem(hotbarItem);
			ItemSlotType hotbarSlotType = ItemSlotType.getHotbarSlotType(e.getHotbarButton(), inv.getHeldItemSlot());
			
			if (hotbarGrandItem != null) p.sendMessage("  Hotbar: " + hotbarGrandItem.getName());
			p.sendMessage("  HotbarSlotType: " + hotbarSlotType);
			p.sendMessage("  Current item is swapped with hotbar item");
			
			if (currentGrandItem != null) gInv.putItem(currentItem, currentGrandItem, hotbarSlotType);
			if (hotbarGrandItem != null) gInv.putItem(hotbarItem, hotbarGrandItem, clickedSlotType);
		}
		
		//Current item displaces hotbar item, which is readded to inventory 
		if (e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
			p.sendMessage("  Current item displaces hotbar item, which is readded to inventory ");
			//TODO: Figure out where the items will be moved to
			resetInventoryNextTick(p);
		}
		
		p.sendMessage("------------------------------");
	}
	
	private void registerPlayer(Player p) {
		GrandInventory gInv = new GrandInventory();
		gInv.resetToPlayer(p);
		playerInventories.put(p.getName(), gInv);
	}
	
	private void resetInventoryNextTick(final Player p) {
		new BukkitRunnable() { @Override public void run() {
			playerInventories.get(p.getName()).resetToPlayer(p);
		}}.runTaskLater(plugin, 1L);
	}
	
	//TODO: InventoryDragEvent, InventoryPickupItemEvent, PlayerDropItemEvent
	//TODO: Others as needed
	
}
