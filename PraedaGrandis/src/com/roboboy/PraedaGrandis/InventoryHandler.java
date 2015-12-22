package com.roboboy.PraedaGrandis;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
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
		
		GrandItem oldGrandItem = plugin.itemHandler.matchItem(playerItems[e.getPreviousSlot()]);
		if (oldGrandItem != null) {
			gInv.removeItem(oldGrandItem, ItemSlotType.HELD);
			gInv.addItem(oldGrandItem, ItemSlotType.UNHELD);
		}
		
		GrandItem newGrandItem = plugin.itemHandler.matchItem(playerItems[e.getNewSlot()]);
		if (newGrandItem != null) {
			gInv.removeItem(newGrandItem, ItemSlotType.UNHELD);
			gInv.addItem(newGrandItem, ItemSlotType.HELD);
		}
	}
	
	//Player 
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(InventoryClickEvent e)
	{
		if (!(e.getWhoClicked() instanceof Player)) return; //Is this even possible?
		if (!e.getWhoClicked().getInventory().equals(e.getClickedInventory()) && e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) return;
		if (e.getAction() == InventoryAction.NOTHING || e.getAction() == InventoryAction.CLONE_STACK || e.getAction() == InventoryAction.UNKNOWN) return;
		
		final Player p = (Player) e.getWhoClicked();
		new BukkitRunnable() { @Override public void run() {
			registerPlayer(p);
		}}.runTaskLater(plugin, 1);
		/*if (!e.getWhoClicked().getInventory().equals(e.getClickedInventory())) return;
		if (e.getAction() == InventoryAction.UNKNOWN || e.getAction() == InventoryAction.NOTHING || e.getAction() == InventoryAction.CLONE_STACK) return;
		
		GrandItem currentGrandItem = plugin.itemHandler.matchItem(e.getCurrentItem());
		GrandItem cursorGrandItem = plugin.itemHandler.matchItem(e.getCursor());
		ItemSlotType clickedSlotType = ItemSlotType.getSlotType(e.getSlotType(), e.getSlot(), e.getWhoClicked().getInventory().getHeldItemSlot());
		if (currentGrandItem == null && cursorGrandItem == null) return;
		if (clickedSlotType.isNull()) return;
		
		GrandInventory gInv = playerInventories.get(e.getWhoClicked().getName());
		
		//Current item is removed from inventory
		if (currentGrandItem != null && (
				e.getAction() == InventoryAction.COLLECT_TO_CURSOR || e.getAction() == InventoryAction.DROP_ALL_SLOT
				|| e.getAction() == InventoryAction.DROP_ONE_SLOT || e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD
				|| e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
				|| e.getAction() == InventoryAction.PICKUP_ALL || e.getAction() == InventoryAction.PICKUP_HALF
				|| e.getAction() == InventoryAction.PICKUP_ONE || e.getAction() == InventoryAction.PICKUP_SOME
				|| e.getAction() == InventoryAction.SWAP_WITH_CURSOR)
				) {
			gInv.removeItem(currentGrandItem, clickedSlotType);
		}
		
		//Cursor item is added to inventory
		if (cursorGrandItem != null && (
				e.getAction() == InventoryAction.PLACE_ALL || e.getAction() == InventoryAction.PLACE_ONE
				|| e.getAction() == InventoryAction.PLACE_SOME || e.getAction() == InventoryAction.SWAP_WITH_CURSOR)) {
			gInv.removeItem(cursorGrandItem, clickedSlotType);
		}*/
	}
	
	private void registerPlayer(Player p) {
		GrandInventory gInv = new GrandInventory();
		playerInventories.put(p.getName(), gInv);
		
		//Read all grand items from player's inventory
		for (ItemSlotType type : ItemSlotType.getUniqueTypes()) {
			for (ItemStack item : type.getItems(p)) {
				GrandItem gItem = plugin.itemHandler.matchItem(item);
				if (gItem != null) gInv.addItem(gItem, type);
			}
		}
	}
	
	//TODO: InventoryDragEvent, InventoryPickupItemEvent
	//TODO: Others as needed
	
}
