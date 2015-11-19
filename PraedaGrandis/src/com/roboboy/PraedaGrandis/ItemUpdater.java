package com.roboboy.PraedaGrandis;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;

/**
 * Updates all items in player inventory on login and reload. Updates
 * single items when a player clicks on the item in their inventory.
 * @author roboboy
 *
 */
public class ItemUpdater implements Listener
{
	private final PraedaGrandis plugin;
	
	public ItemUpdater(PraedaGrandis plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		updateItems(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(final InventoryClickEvent e)
	{
		//if (!(e instanceof InventoryCreativeEvent))
		//{
			//updateItem(e.getCurrentItem());
			//updateItem(e.getCursor());
			if (e.getWhoClicked() instanceof Player) {
				//Player p = (Player) e.getWhoClicked();
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						updateItem(e.getCurrentItem());
						updateItem(e.getCursor());
						//((Player) e.getWhoClicked()).updateInventory();
					}
				}, 1L);
			}
		//}
	}
	
	public void reload()
	{
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			updateItems(p);
		}
	}
	
	private void updateItems(Player p)
	{
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
			GrandItem gItem = plugin.itemHandler.matchItem(item);
			if (gItem != null) {
				gItem.update(item);
			}
			else
			{
				AutoConvertItem cItem = plugin.itemHandler.matchConvertItem(item);
				if (cItem != null) {
					cItem.convert(item);
				}
			}
		}
	}
}
