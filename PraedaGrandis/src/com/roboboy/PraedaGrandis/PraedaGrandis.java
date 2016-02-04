package com.roboboy.PraedaGrandis;

import java.util.EnumSet;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import com.roboboy.PraedaGrandis.Commands.MainCommandExecutor;
import com.roboboy.PraedaGrandis.Configuration.ConfigManager;
import com.roboboy.PraedaGrandis.Configuration.GrandAbilityHandler;
import com.roboboy.PraedaGrandis.Configuration.ItemHandler;

public class PraedaGrandis extends JavaPlugin
{
	static public final Random RANDOM_GENERATOR = new Random();
	static public final EnumSet<Material> CLICK_STEALERS = EnumSet.of(Material.FURNACE, Material.CHEST, Material.BEACON,
			Material.DISPENSER, Material.DROPPER, Material.HOPPER, Material.WORKBENCH, Material.ENCHANTMENT_TABLE,
			Material.ENDER_CHEST, Material.ANVIL, Material.BED_BLOCK, Material.FENCE_GATE, Material.SPRUCE_FENCE_GATE,
			Material.BIRCH_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.DARK_OAK_FENCE_GATE,
			Material.IRON_DOOR_BLOCK, Material.WOODEN_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR,
			Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.WOOD_BUTTON, Material.STONE_BUTTON, Material.TRAP_DOOR,
			Material.IRON_TRAPDOOR, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON, Material.REDSTONE_COMPARATOR_OFF,
			Material.REDSTONE_COMPARATOR_ON, Material.FENCE, Material.SPRUCE_FENCE, Material.BIRCH_FENCE, Material.JUNGLE_FENCE,
			Material.DARK_OAK_FENCE, Material.ACACIA_FENCE, Material.NETHER_FENCE, Material.BREWING_STAND, Material.CAULDRON,
			Material.SIGN_POST, Material.WALL_SIGN, Material.SIGN, Material.CAKE_BLOCK);
	
	public static PraedaGrandis plugin;
	public static final String META_GRANDABILITY_PREFIX = "PG.GrandAbility.";
	public static final String STORAGE_ITEM_NAME = "PraedaGrandis.GrandItemName";
	public static final String STORAGE_ITEM_ID = "PraedaGrandis.GrandItemID";
	//public static final UUID ID = UUID.fromString("2b56453f-6eec-4313-8424-4d5b6c456c70");
	
	private final ConfigManager configManager = ConfigManager.getInstance();
	
	public final GrandAbilityHandler abilityHandler = new GrandAbilityHandler(this);
	public final ItemHandler itemHandler = new ItemHandler(this);
	public final InventoryHandler inventoryHandler = new InventoryHandler(this);
	
	private final ItemUpdater itemUpdater = new ItemUpdater(this);
	private final ActivatorListener activatorListener = new ActivatorListener(this);
	private final ProjectileListener projectileListener = new ProjectileListener(this);
	
	private BukkitTask timerCheckingTask;
	
	//Plugin startup
	@Override
	public void onEnable()
	{
		plugin = this;
		getServer().getPluginManager().registerEvents(itemUpdater, this);
		getServer().getPluginManager().registerEvents(inventoryHandler, this);
		getServer().getPluginManager().registerEvents(activatorListener, this);
		getServer().getPluginManager().registerEvents(projectileListener, this);
		
		//Initial (re)load
		reload();
		
		getCommand(MainCommandExecutor.COMMAND_NAME).setExecutor(new MainCommandExecutor());
	}

	//Plugin disable
	@Override
	public void onDisable()
	{
		//Remove projectiles spawned by ProjectileAbility
		projectileListener.removeAbilityProjectiles();
		//Cancel timer checker
		if (timerCheckingTask != null) timerCheckingTask.cancel();
		//Set plugin to null
		plugin = null;
	}
	
	public void reload() {
		configManager.reload();
		abilityHandler.reload();
		itemHandler.reload();
		itemUpdater.reload();
		inventoryHandler.reload();
		
		//Timer checker
		if (timerCheckingTask != null) timerCheckingTask.cancel();
		timerCheckingTask = new BukkitRunnable() { @Override public void run() {
			for (Player p : getServer().getOnlinePlayers()) {
        		for (GrandInventory.InventoryElement element : inventoryHandler.getItemsFromPlayer(p).getItems()) {
        			element.grandItem.activateTimers(p);
        		}
        	}
		}}.runTaskTimer(plugin, 0L, configManager.getTimerHandlerDelay());
	}
	
}
