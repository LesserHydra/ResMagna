package com.lesserhydra.praedagrandis;

import com.lesserhydra.praedagrandis.arguments.ArmorType;
import com.lesserhydra.praedagrandis.arguments.ItemSlotType;
import com.lesserhydra.praedagrandis.configuration.GrandItem;
import com.lesserhydra.praedagrandis.configuration.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Dispenser;

import java.util.HashMap;
import java.util.Map;

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
	
	//Player respawns
	@EventHandler(priority = EventPriority.MONITOR)
	public void onRespawn(PlayerRespawnEvent event) {
		getItemsFromPlayer(event.getPlayer()).resetToPlayer();
	}
	
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
		
		Player player = e.getPlayer();
		if (grandItem != null) Bukkit.getScheduler().runTask(PraedaGrandis.plugin, () -> addItem(player, item, grandItem));
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
		
		ItemStack item = e.getOldCursor();
		GrandItem grandItem = ItemHandler.getInstance().matchItem(item);
		if (grandItem != null) Bukkit.getScheduler().runTask(PraedaGrandis.plugin, () -> addItem(player, item, grandItem));
	}
	
	//Player "clicks" with inventory open
	//TODO: Implement in a nicer way
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) return;
		Player p = (Player)e.getWhoClicked();
		
		if (e.getAction() == InventoryAction.NOTHING || e.getAction() == InventoryAction.CLONE_STACK
				|| e.getAction() == InventoryAction.UNKNOWN) return;
		
		//DEBUG: GrandLogger.log(e.getAction().name(), LogType.DEBUG);
		
		PlayerInventory inv = p.getInventory();
		GrandInventory gInv = playerInventories.get(p.getName());
		ItemStack currentItem = e.getCurrentItem();
		ItemStack cursorItem = e.getCursor();
		GrandItem currentGrandItem = ItemHandler.getInstance().matchItem(currentItem);
		GrandItem cursorGrandItem = ItemHandler.getInstance().matchItem(cursorItem);
		
		boolean creative = e instanceof InventoryCreativeEvent;
		boolean otherInventory = !inv.equals(e.getClickedInventory());
		
		//Assumes own inventory
		ItemSlotType clickedSlotType = creative
				? ItemSlotType.fromCreativeIndex(e.getSlot(), inv.getHeldItemSlot())
				: ItemSlotType.fromTotalIndex(e.getSlot(), inv.getHeldItemSlot());
		
		//Special case for most creative events
		if (creative && currentGrandItem != null && e.getAction() == InventoryAction.PLACE_ALL) {
			/*The main issue here is that swaps (and shifting within own inventory) show up as two events, one before
			the swap and one after it. For example, when shift-equipping a helmet, the first event will decide to add
			the item to HELMET and the second will remove it.*/
			addItemAtEndOfTick(p, currentItem, currentGrandItem);
		}
		
		//Shift-clicked
		if (currentGrandItem != null && e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			addItemAtEndOfTick(p, currentItem, currentGrandItem);
			return; //Done
		}
		
		//Current item is swapped with hotbar item
		if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
			ItemStack hotbarItem = inv.getItem(e.getHotbarButton());
			GrandItem hotbarGrandItem = ItemHandler.getInstance().matchItem(hotbarItem);
			ItemSlotType hotbarSlotType = creative
					? ItemSlotType.fromCreativeIndex(e.getHotbarButton(), inv.getHeldItemSlot())
					: ItemSlotType.fromTotalIndex(e.getHotbarButton(), inv.getHeldItemSlot());
			
			if (currentGrandItem != null) gInv.putItem(currentItem, currentGrandItem, hotbarSlotType);
			if (hotbarGrandItem != null) {
				if (otherInventory) gInv.removeItem(hotbarItem);
				else gInv.putItem(hotbarItem, hotbarGrandItem, clickedSlotType);
			}
			return; //Done
		}
		
		/*Seems to happen when hotbar swapping two items when current is in another inventory. Also fires when current
		is in armor slots and hotbar is not armor, but the items don't actually swap for some reason. The old behavior
		had the hotbar item being readded to inventory.*/
		if (e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
			ItemStack hotbarItem = inv.getItem(e.getHotbarButton());
			GrandItem hotbarGrandItem = ItemHandler.getInstance().matchItem(hotbarItem);
			if (currentGrandItem != null) addItemAtEndOfTick(p, currentItem, currentGrandItem);
			if (hotbarGrandItem != null) addItemAtEndOfTick(p, hotbarItem, hotbarGrandItem);
			return; //Done
		}
		
		//Clicked on other inventory
		if (otherInventory) return;
		
		//Current item is removed from inventory
		if (currentGrandItem != null
				&& (e.getAction() == InventoryAction.COLLECT_TO_CURSOR || e.getAction() == InventoryAction.PICKUP_ALL
				|| e.getAction() == InventoryAction.PICKUP_HALF || e.getAction() == InventoryAction.PICKUP_ONE
				|| e.getAction() == InventoryAction.PICKUP_SOME || e.getAction() == InventoryAction.SWAP_WITH_CURSOR)) {
			gInv.removeItem(currentItem);
		}
		
		//Current item is dropped (only works if hand is empty?)
		if (currentGrandItem != null && (cursorItem == null || cursorItem.getType() == Material.AIR)
				&& (e.getAction() == InventoryAction.DROP_ALL_SLOT || e.getAction() == InventoryAction.DROP_ONE_SLOT)) {
			gInv.removeItem(currentItem);
		}
		
		//Cursor item is added to inventory
		if (cursorGrandItem != null
				&& (e.getAction() == InventoryAction.PLACE_ALL || e.getAction() == InventoryAction.PLACE_ONE
				|| e.getAction() == InventoryAction.PLACE_SOME || e.getAction() == InventoryAction.SWAP_WITH_CURSOR)) {
			gInv.putItem(cursorItem, cursorGrandItem, clickedSlotType);
		}
	}
	
	//Based loosely off of https://github.com/Borlea/ArmorEquipEvent
	@EventHandler(priority = EventPriority.MONITOR)
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
	@EventHandler(priority = EventPriority.MONITOR)
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
			//In case multiple players are valid
			if (playerIsTouchingBlock(targetBlock, p) && type.isEmpty(p)) addItemAtEndOfTick(p, item, grandItem);
		}
	}
	
	//TODO: Place in itemframe or armorstand
	//TODO: Use? (eat/place/break)
	
	private boolean playerIsTouchingBlock(Block block, Player player) {
		Location playerLocation = player.getLocation();
		double playerX = playerLocation.getX();
		double playerY = playerLocation.getY();
		double playerZ = playerLocation.getZ();
		
		return !(playerX < block.getX() - 0.3 || playerX > block.getX() + 1.3)
				&& !(playerZ < block.getZ() - 0.3 || playerZ > block.getZ() + 1.3)
				&& !(playerY < block.getY() - 1.8 || playerY > block.getY());
	}
	
	private void registerPlayer(Player p) {
		GrandInventory gInv = new GrandInventory(p);
		gInv.resetToPlayer();
		playerInventories.put(p.getName(), gInv);
	}
	
	private void addItemAtEndOfTick(Player player, ItemStack item, GrandItem grandItem) {
		Bukkit.getScheduler().runTask(PraedaGrandis.plugin, () -> addItem(player, item, grandItem));
	}
	
	private void addItem(Player player, ItemStack item, GrandItem grandItem) {
		GrandInventory gInv = playerInventories.get(player.getName());
		ItemSlotType slot = ItemSlotType.find(player, item);
		gInv.putItem(item, grandItem, slot);
	}
	
}
