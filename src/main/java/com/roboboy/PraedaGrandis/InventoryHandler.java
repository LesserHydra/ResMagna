package com.roboboy.PraedaGrandis;

import java.util.HashMap;
import java.util.Map;

import com.roboboy.PraedaGrandis.Arguments.ArmorType;
import com.roboboy.PraedaGrandis.Arguments.ItemSlotType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dispenser;
import org.bukkit.scheduler.BukkitRunnable;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;
import com.roboboy.PraedaGrandis.Configuration.ItemHandler;

public class InventoryHandler implements Listener {
	
	private static InventoryHandler instance = new InventoryHandler();
	private InventoryHandler() {}
	public static InventoryHandler getInstance() {
		return instance;
	}
	
	private Map<String, GrandInventory> playerInventories = new HashMap<>();
	
	/**
	 * Forgets about previously handled inventories, and treats all
	 * players as if they had just logged on.<br>
	 * <br>
	 * <strong>The ItemHandler must be reloaded first.</strong>
	 */
	void reload() {
		playerInventories.clear();
		PraedaGrandis.plugin.getServer().getOnlinePlayers().forEach(this::registerPlayer);
	}
	
	public GrandInventory getItemsFromPlayer(Player p) { return playerInventories.get(p.getName()); }
	
	//Player logs in
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) { registerPlayer(e.getPlayer()); }
	
	//Player changes selected item
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChangeSelected(PlayerItemHeldEvent e) {
		GrandInventory gInv = playerInventories.get(e.getPlayer().getName());
		ItemStack[] playerItems = e.getPlayer().getInventory().getContents();
		
		ItemStack oldItem = playerItems[e.getPreviousSlot()];
		GrandItem oldGrandItem = ItemHandler.getInstance().matchItem(oldItem);
		if (oldGrandItem != null) {
			gInv.putItem(oldItem, oldGrandItem, ItemSlotType.UNHELD);
		}
		
		ItemStack newItem = playerItems[e.getNewSlot()];
		GrandItem newGrandItem = ItemHandler.getInstance().matchItem(newItem);
		if (newGrandItem != null) {
			gInv.putItem(newItem, newGrandItem, ItemSlotType.HELDMAIN);
		}
	}
	
	//Player swaps main and offhand items
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSwapHands(PlayerSwapHandItemsEvent event) {
		GrandInventory gInv = playerInventories.get(event.getPlayer().getName());
		
		ItemStack mainHandItem = event.getMainHandItem();
		GrandItem mainHandGrand = ItemHandler.getInstance().matchItem(mainHandItem);
		if (mainHandGrand != null) {
			gInv.putItem(mainHandItem, mainHandGrand, ItemSlotType.HELDMAIN);
		}
		
		ItemStack offHandItem = event.getOffHandItem();
		GrandItem offHandGrand = ItemHandler.getInstance().matchItem(offHandItem);
		if (offHandGrand != null) {
			gInv.putItem(offHandItem, offHandGrand, ItemSlotType.HELDOFF);
		}
	}
	
	//Player picks up an item
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemPickup(PlayerPickupItemEvent e) {
		ItemStack item = e.getItem().getItemStack();
		GrandItem grandItem = ItemHandler.getInstance().matchItem(item);
		if (grandItem == null) return;
		
		//TODO: Figure out where the item will added to
		resetInventoryNextTick(e.getPlayer());
	}
	
	//Player drops an item
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemDrop(PlayerDropItemEvent e) {
		ItemStack item = e.getItemDrop().getItemStack();
		GrandItem grandItem = ItemHandler.getInstance().matchItem(item);
		if (grandItem == null) return;
		
		Player player = e.getPlayer();
		GrandInventory gInv = playerInventories.get(player.getName());
		gInv.removeItem(item);
	}
	
	//Player drags with inventory open
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryDrag(InventoryDragEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) return;
		Player player = (Player)e.getWhoClicked();
		
		ItemStack cursorItem = e.getOldCursor();
		GrandItem grandCursorItem = ItemHandler.getInstance().matchItem(cursorItem);
		if (grandCursorItem == null) return;
		
		//TODO: Figure out where the item will added to
		resetInventoryNextTick(player);
	}
	
	//Player "clicks" with inventory open
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(InventoryClickEvent e)
	{
		if (!(e.getWhoClicked() instanceof Player)) return;
		Player p = (Player)e.getWhoClicked();
		
		if (e instanceof InventoryCreativeEvent) {
			resetInventoryNextTick(p);
			return;
		}
		
		if (e.getAction() == InventoryAction.NOTHING || e.getAction() == InventoryAction.CLONE_STACK || e.getAction() == InventoryAction.UNKNOWN) return;
		
		//FIXME: Fix for 1.9
		//FIXME: Offhand slot seems to be slot=9,raw=45,inv=player
		resetInventoryNextTick(p);
		
		/*PlayerInventory inv = p.getInventory();
		GrandInventory gInv = playerInventories.get(p.getName());
		ItemStack currentItem = e.getCurrentItem();
		ItemStack cursorItem = e.getCursor();
		GrandItem currentGrandItem = ItemHandler.getInstance().matchItem(currentItem);
		GrandItem cursorGrandItem = ItemHandler.getInstance().matchItem(cursorItem);
		ItemSlotType clickedSlotType = ItemSlotType.getSlotType(e.getSlotType(), e.getSlot(), inv.getHeldItemSlot());
		
		//if (currentGrandItem == null && cursorGrandItem == null) return;
		
		//Clicked on other inventory
		if (!inv.equals(e.getClickedInventory())) {
			//TODO: Figure out where the item will be moved to
			if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
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
				resetInventoryNextTick(p);
			}
			//Current item is shift-clicked to another inventory
			else if (topInventoryType == InventoryType.ANVIL || topInventoryType == InventoryType.CHEST
					|| topInventoryType == InventoryType.DISPENSER || topInventoryType == InventoryType.DROPPER
					|| topInventoryType == InventoryType.ENCHANTING || topInventoryType == InventoryType.ENDER_CHEST
					|| topInventoryType == InventoryType.HOPPER){
				//TODO: Check for failure (inventory is full)
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
			gInv.removeItem(currentItem);
		}
		
		//Cursor item is added to inventory
		if (cursorGrandItem != null && (
				e.getAction() == InventoryAction.PLACE_ALL || e.getAction() == InventoryAction.PLACE_ONE
				|| e.getAction() == InventoryAction.PLACE_SOME || e.getAction() == InventoryAction.SWAP_WITH_CURSOR)) {
			gInv.putItem(cursorItem, cursorGrandItem, clickedSlotType);
		}
		
		//Current item is swapped with hotbar item
		if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
			ItemStack hotbarItem = inv.getItem(e.getHotbarButton());
			GrandItem hotbarGrandItem = ItemHandler.getInstance().matchItem(hotbarItem);
			ItemSlotType hotbarSlotType = ItemSlotType.getHotbarSlotType(e.getHotbarButton(), inv.getHeldItemSlot());
			
			if (currentGrandItem != null) gInv.putItem(currentItem, currentGrandItem, hotbarSlotType);
			if (hotbarGrandItem != null) gInv.putItem(hotbarItem, hotbarGrandItem, clickedSlotType);
		}
		
		//Current item displaces hotbar item, which is readded to inventory 
		if (e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
			//TODO: Figure out where the items will be moved to
			resetInventoryNextTick(p);
		}*/
	}
	
	//Based loosely off of https://github.com/Borlea/ArmorEquipEvent
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		ItemStack item = e.getItem();
		if (item == null) return;
		
		ArmorType newArmorType = ArmorType.fromMaterial(item.getType());
		if (newArmorType.isNull() || newArmorType == ArmorType.BLOCK) return;
		
		Player player = e.getPlayer();
		if (!newArmorType.isEmpty(player)) return;
		
		GrandItem grandItem = ItemHandler.getInstance().matchItem(item);
		if (grandItem == null) return;
		
		if (e.getClickedBlock() != null && !player.isSneaking()) {
			//Some blocks have actions when you right click them which stops the client from equipping the armor in hand.
			if (PraedaGrandis.CLICK_STEALERS.contains(e.getClickedBlock().getType())) return;
		}
		
		GrandInventory gInv = playerInventories.get(player.getName());
		gInv.putItem(item, grandItem, newArmorType.getItemSlotType());
	}
	
	//Based loosely off of https://github.com/Borlea/ArmorEquipEvent
	@EventHandler
	public void onDispenserFire(BlockDispenseEvent e) {
		ItemStack item = e.getItem();
		if (item == null) return;
		
		ArmorType type = ArmorType.fromMaterial(item.getType());
		if (type.isNull()) return;
		
		GrandItem grandItem = ItemHandler.getInstance().matchItem(item);
		if (grandItem == null) return;
		
		Location loc = e.getBlock().getLocation();
		BlockFace directionFacing = ((Dispenser) e.getBlock().getState().getData()).getFacing();
		Block targetBlock = e.getBlock().getRelative(directionFacing);
		
		for (Player p : loc.getWorld().getPlayers()) {
			if (!type.isEmpty(p)) continue;
			if (!playerIsTouchingBlock(targetBlock, p)) continue;
			
			GrandInventory gInv = playerInventories.get(p.getName());
			gInv.putItem(item, grandItem, type.getItemSlotType());
			return; //TODO: What happens if two players are valid?
		}
	}
	
	private boolean playerIsTouchingBlock(Block block, Player player) {
		Location playerLocation = player.getLocation();
		double playerX = playerLocation.getX();
		double playerY = playerLocation.getY();
		double playerZ = playerLocation.getZ();
		
		if (playerX < block.getX() - 0.3 || playerX > block.getX() + 1.3)	return false;
		if (playerZ < block.getZ() - 0.3 || playerZ > block.getZ() + 1.3)	return false;
		if (playerY < block.getY() - 1.8 || playerY > block.getY())			return false;
		
		return true;
	}
	
	private void registerPlayer(Player p) {
		GrandInventory gInv = new GrandInventory(p);
		gInv.resetToPlayer();
		playerInventories.put(p.getName(), gInv);
	}
	
	private void resetInventoryNextTick(final Player p) {
		new BukkitRunnable() { @Override public void run() {
			playerInventories.get(p.getName()).resetToPlayer();
		}}.runTaskLater(PraedaGrandis.plugin, 1L);
	}
	
}
