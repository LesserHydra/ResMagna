package com.lesserhydra.praedagrandis;

import com.lesserhydra.praedagrandis.configuration.AutoConvertItem;
import com.lesserhydra.praedagrandis.configuration.GrandItem;
import com.lesserhydra.praedagrandis.configuration.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

/**
 * Updates all items in player inventory on login and reload. Updates
 * single items when a player clicks on the item in an inventory.
 */
class ItemUpdater implements Listener {
	private final PraedaGrandis plugin;
	
	ItemUpdater(PraedaGrandis plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		VariableHandler.registerPlayer(p);
		
		//Schedule items for updating
		Bukkit.getScheduler().runTask(plugin, () -> updateItems(p));
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event instanceof InventoryCreativeEvent) return;
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		ItemStack item = event.getCurrentItem();
		ItemStack updated = updateItem(item);
		if (updated != null) {
			event.setCurrentItem(updated);
			((Player) event.getWhoClicked()).updateInventory();
		}
	}
	
	/**
	 * Updates all items in each player's inventory.<br>
	 * <br>
	 * <strong>The ItemHandler must be reloaded first.</strong>
	 */
	void reload() {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			updateItems(p);
			VariableHandler.registerPlayer(p);
		}
	}
	
	//Keep items from being used in crafting
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCraftingEvent(CraftItemEvent event) {
		for (ItemStack item : event.getInventory().getMatrix()) {
			GrandItem gItem = ItemHandler.getInstance().matchItem(item);
			if (gItem == null) continue;
			
			event.setCancelled(true);
			return;
		}
	}
	
	//Keeps items from being placed as blocks
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		
		GrandItem gItem = ItemHandler.getInstance().matchItem(item);
		if (gItem == null || gItem.isPlaceable()) return;
		
		event.setCancelled(true);
		
		Player player = event.getPlayer();
		Bukkit.getScheduler().runTask(plugin, player::updateInventory);
	}

	//Keeps persistant items from despawning
	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemDespawn(ItemDespawnEvent event) {
		Item item = event.getEntity();
		if (!item.isCustomNameVisible()) return;
		GrandItem gItem = ItemHandler.getInstance().matchItem(item.getItemStack());
		if (gItem == null || !gItem.isPersistant()) return;
		
		event.setCancelled(true);
		item.setTicksLived(1);
	}
	
	//Keeps persistant items from breaking
	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemBreak(PlayerItemBreakEvent event) {
		final ItemStack item = event.getBrokenItem();
		GrandItem gItem = ItemHandler.getInstance().matchItem(item);
		if (gItem == null || !gItem.isPersistant()) return;
		
		item.setAmount(1);
		
		Player player = event.getPlayer();
		Bukkit.getScheduler().runTask(plugin, () -> {
			item.setDurability((short) (item.getType().getMaxDurability() + 1));
			player.updateInventory();
		});
	}
	
	//Keeps broken persistant items from breaking blocks
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockDamageEvent event) {
		ItemStack item = event.getItemInHand();
		
		GrandItem gItem = ItemHandler.getInstance().matchItem(item);
		if (gItem == null || !gItem.isPersistant()) return;
		if (item.getDurability() != item.getType().getMaxDurability() + 1) return;
		
		event.setCancelled(true);
		
		Player player = event.getPlayer();
		Bukkit.getScheduler().runTask(plugin, player::updateInventory);
	}
	
	//Gives dropped persistent items a custom name
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemDropped(ItemSpawnEvent event) {
		Item drop = event.getEntity();
		GrandItem gItem = ItemHandler.getInstance().matchItem(drop.getItemStack());
		if (gItem == null || !gItem.isPersistant()) return;
		
		drop.setCustomName(gItem.getDisplayName());
		drop.setCustomNameVisible(true);
	}
	
	private void updateItems(Player player) {
		if (!player.isOnline()) return;
		
		PlayerInventory inventory = player.getInventory();
		ItemStack[] contents = inventory.getContents();
		for (int i = 0; i < contents.length; ++i) {
			ItemStack newItem = updateItem(contents[i]);
			if (newItem != null) inventory.setItem(i, newItem);
		}
		player.updateInventory();
	}
	
	@Nullable
	private ItemStack updateItem(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) return null;
		
		GrandItem gItem = ItemHandler.getInstance().matchItem(item);
		if (gItem != null) return gItem.update(item);
		
		AutoConvertItem cItem = ItemHandler.getInstance().matchConvertItem(item);
		if (cItem != null) return cItem.convert(item);
		
		return null;
	}
	
}
