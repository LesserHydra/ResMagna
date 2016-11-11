package com.lesserhydra.praedagrandis;

import com.lesserhydra.praedagrandis.configuration.AutoConvertItem;
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
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import com.lesserhydra.praedagrandis.configuration.GrandItem;
import com.lesserhydra.praedagrandis.configuration.ItemHandler;

/**
 * Updates all items in player inventory on login and reload. Updates
 * single items when a player clicks on the item in their inventory.
 * @author roboboy
 *
 */
public class ItemUpdater implements Listener
{
	private final PraedaGrandis plugin;
	
	public ItemUpdater(PraedaGrandis plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		VariableHandler.registerPlayer(p);
		
		//Schedual items for updating
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				updateItems(p);
			}
		}, 1L);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(final InventoryClickEvent e)
	{
		if (!(e.getWhoClicked() instanceof Player)) return;
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				updateItem(e.getCurrentItem());
				updateItem(e.getCursor());
			}
		}, 1L);
	}
	
	/**
	 * Updates all items in each player's inventory.<br>
	 * <br>
	 * <strong>The ItemHandler must be reloaded first.</strong>
	 */
	public void reload()
	{
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			updateItems(p);
			VariableHandler.registerPlayer(p);
		}
	}
	
	//Keep items from being used in crafting
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCraftingEvent(CraftItemEvent e) {
		for (ItemStack item : e.getInventory().getMatrix()) {
			GrandItem gItem = ItemHandler.getInstance().matchItem(item);
			if (gItem != null) {
				e.setCancelled(true);
			}
		}
	}
	
	//Keeps items from being placed as blocks
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent e) {
		ItemStack item = e.getItemInHand();
		
		GrandItem gItem = ItemHandler.getInstance().matchItem(item);
		if (gItem == null || gItem.isPlaceable()) return;
		
		e.setCancelled(true);
		updateInventoryNextTick(e.getPlayer());
	}

	//Keeps persistant items from despawning
	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemDespawn(ItemDespawnEvent e) {
		Item item = e.getEntity();
		if (!item.isCustomNameVisible()) return;
		GrandItem gItem = ItemHandler.getInstance().matchItem(item.getItemStack());
		if (gItem == null || !gItem.isPersistant()) return;
		
		e.setCancelled(true);
		item.setTicksLived(1);
	}
	
	//Keeps persistant items from breaking
	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemBreak(PlayerItemBreakEvent e) {
		final ItemStack item = e.getBrokenItem();
		GrandItem gItem = ItemHandler.getInstance().matchItem(item);
		if (gItem == null || !gItem.isPersistant()) return;
		
		item.setAmount(1);
		
		final Player p = e.getPlayer();
		new BukkitRunnable() { @Override public void run() {
			item.setDurability((short) (item.getType().getMaxDurability() + 1));
			p.updateInventory();
		}}.runTaskLater(plugin, 1L);
	}
	
	//Keeps broken persistant items from breaking blocks
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockDamageEvent e) {
		ItemStack item = e.getItemInHand();
		
		GrandItem gItem = ItemHandler.getInstance().matchItem(item);
		if (gItem == null || !gItem.isPersistant()) return;
		if (item.getDurability() != item.getType().getMaxDurability() + 1) return;
		
		e.setCancelled(true);
		updateInventoryNextTick(e.getPlayer());
	}
	
	//Gives dropped persistant items a custom name
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemDropped(ItemSpawnEvent e) {
		Item drop = e.getEntity();
		GrandItem gItem = ItemHandler.getInstance().matchItem(drop.getItemStack());
		if (gItem == null || !gItem.isPersistant()) return;
		
		drop.setCustomName(gItem.getDisplayName());
		drop.setCustomNameVisible(true);
	}
	
	private void updateItems(Player p)
	{
		if (!p.isOnline()) return;
		
		for (ItemStack item : p.getInventory().getContents()) {
			updateItem(item);
		}
		for (ItemStack item : p.getInventory().getArmorContents()) {
			updateItem(item);
		}
		p.updateInventory();
	}
	
	//TODO: Check if item actually NEEDS to be updated...
	private void updateItem(ItemStack item)
	{
		if (item != null && item.getType() != Material.AIR)
		{
			GrandItem gItem = ItemHandler.getInstance().matchItem(item);
			if (gItem != null) {
				gItem.update(item);
			}
			else
			{
				AutoConvertItem cItem = ItemHandler.getInstance().matchConvertItem(item);
				if (cItem != null) {
					cItem.convert(item);
				}
			}
		}
	}
	
	private void updateInventoryNextTick(final Player player) {
		new BukkitRunnable() { @Override public void run() {
			player.updateInventory();
		}}.runTaskLater(plugin, 1);
	}
}
