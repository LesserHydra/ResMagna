package com.lesserhydra.resmagna;

import com.lesserhydra.resmagna.configuration.AutoConvertItem;
import com.lesserhydra.resmagna.configuration.GrandItem;
import com.lesserhydra.resmagna.configuration.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
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
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Calls item updates.
 */
class ItemUpdater implements Listener {
	private final ResMagna plugin;
	
	ItemUpdater(ResMagna plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		VariableHandler.registerPlayer(player);
		
		//Schedule items for updating
		Bukkit.getScheduler().runTask(plugin, () -> {
			if (!player.isValid()) return;
			updateItemsInInventory(player.getInventory());
			player.updateInventory();
		});
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onChunkLoad(ChunkLoadEvent event) {
		//Update items in tile entity and entity (excluding players) inventories
		Chunk chunk = event.getChunk();
		Arrays.stream(chunk.getTileEntities())
				.filter(tile -> tile instanceof InventoryHolder)
				.map(tile -> (InventoryHolder) tile)
				.map(InventoryHolder::getInventory)
				.forEach(this::updateItemsInInventory);
		//TODO: Update items on ground
		Arrays.stream(chunk.getEntities())
				.filter(e -> e instanceof InventoryHolder)
				.filter(e -> !(e instanceof Player))
				.map(e -> (InventoryHolder) e)
				.map(InventoryHolder::getInventory)
				.forEach(this::updateItemsInInventory);
	}
	
	/**
	 * Updates all items in each player's inventory.<br>
	 * <br>
	 * <strong>The ItemHandler must be reloaded first.</strong>
	 */
	void reload() {
		//Update items in tile entity and entity (including players) inventories
		for (World world : Bukkit.getWorlds()) {
			Arrays.stream(world.getLoadedChunks())
					.flatMap(chunk -> Arrays.stream(chunk.getTileEntities()))
					.filter(tile -> tile instanceof InventoryHolder)
					.map(tile -> (InventoryHolder) tile)
					.map(InventoryHolder::getInventory)
					.forEach(this::updateItemsInInventory);
			//TODO: Update items on ground
			world.getEntities().stream()
					.filter(e -> e instanceof InventoryHolder)
					.map(e -> (InventoryHolder) e)
					.map(InventoryHolder::getInventory)
					.forEach(this::updateItemsInInventory);
		}
		
		//Send inventory updates and register players
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			p.updateInventory();
			VariableHandler.registerPlayer(p);
		}
		
	}
	
	//Keep items from being used in crafting
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCraftingEvent(PrepareItemCraftEvent event) {
		for (ItemStack item : event.getInventory().getMatrix()) {
			GrandItem gItem = ItemHandler.getInstance().matchItem(item);
			if (gItem == null) continue;
			
			//TODO: Don't cancel if item allows crafting
			event.getInventory().setResult(new ItemStack(Material.AIR));
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
	
	private void updateItemsInInventory(Inventory inventory) {
		ItemStack[] contents = inventory.getContents();
		for (int i = 0; i < contents.length; ++i) {
			ItemStack newItem = updateItem(contents[i]);
			if (newItem != null) inventory.setItem(i, newItem);
		}
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
