package com.roboboy.PraedaGrandis;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import com.roboboy.PraedaGrandis.Abilities.ItemSlotType;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;

public class InventoryHandler implements Listener
{
	private final PraedaGrandis plugin;
	private Map<String, GrandInventory> playerInventories = new HashMap<>();
	
	public InventoryHandler(PraedaGrandis plugin) {
		this.plugin = plugin;
	}
	
	public GrandInventory getItemsFromPlayer(Player p) {
		return playerInventories.get(p.getName());
	}
	
	//Player logs in
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		GrandInventory gInv = new GrandInventory();
		playerInventories.put(p.getName(), gInv);
		
		//Read all grand items from player's inventory
		for (ItemSlotType type : ItemSlotType.getUniqueTypes()) {
			for (ItemStack item : type.getItems(p)) {
				GrandItem gItem = plugin.itemHandler.matchItem(item);
				if (gItem != null) {
					gInv.addItem(gItem, type);
				}
			}
		}
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
	
}
