package com.lesserhydra.resmagna;

import com.lesserhydra.hydracore.HydraCore;
import com.lesserhydra.resmagna.commands.MainCommandExecutor;
import com.lesserhydra.resmagna.configuration.ConfigManager;
import com.lesserhydra.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.EnumSet;
import java.util.Random;

public class ResMagna extends JavaPlugin {
	
	private static final int CORE_MAJOR = 2;
	private static final int CORE_MINOR = 0;
	
	public static final Random RANDOM_GENERATOR = new Random();
	public static final EnumSet<Material> CLICK_STEALERS = EnumSet.of(Material.FURNACE, Material.CHEST, Material.BEACON,
			Material.DISPENSER, Material.DROPPER, Material.HOPPER, Material.WORKBENCH, Material.ENCHANTMENT_TABLE,
			Material.ENDER_CHEST, Material.ANVIL, Material.BED_BLOCK, Material.FENCE_GATE, Material.SPRUCE_FENCE_GATE,
			Material.BIRCH_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.DARK_OAK_FENCE_GATE,
			Material.IRON_DOOR_BLOCK, Material.WOODEN_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR,
			Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.WOOD_BUTTON, Material.STONE_BUTTON, Material.TRAP_DOOR,
			Material.IRON_TRAPDOOR, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON, Material.REDSTONE_COMPARATOR_OFF,
			Material.REDSTONE_COMPARATOR_ON, Material.FENCE, Material.SPRUCE_FENCE, Material.BIRCH_FENCE, Material.JUNGLE_FENCE,
			Material.DARK_OAK_FENCE, Material.ACACIA_FENCE, Material.NETHER_FENCE, Material.BREWING_STAND, Material.CAULDRON,
			Material.SIGN_POST, Material.WALL_SIGN, Material.SIGN, Material.CAKE_BLOCK);
	
	public static ResMagna plugin;
	public static final String META_HOLDER = "PG.Holder";
	public static final String META_GRANDABILITY_PREFIX = "PG.GrandAbility.";
	
	private final ItemUpdater itemUpdater = new ItemUpdater(this);
	private final ActivatorListener activatorListener = new ActivatorListener(this);
	private final ProjectileListener projectileListener = new ProjectileListener(this);
	private final RecipeHandler recipeHandler = new RecipeHandler();
	
	private BukkitTask timerCheckingTask;
	
	//Plugin startup
	@Override
	public void onEnable() {
		assert HydraCore.isLoaded();
		Version.Compat coreCompat = HydraCore.expectVersion(CORE_MAJOR, CORE_MINOR);
		if (coreCompat != Version.Compat.MATCH) {
			if (coreCompat.isOutdated()) {
				getLogger().severe("The loaded version of HydraCore is outdated! Please update to "
						+ CORE_MAJOR + "." + CORE_MINOR + "+.");
				//TODO: Link
			}
			else {
				getLogger().severe("The loaded version of HydraCore is incompatible with this " +
						"version of ResMagna. Please update ResMagna or downgrade HydraCore to "
						+ CORE_MAJOR + "." + CORE_MINOR + "+.");
				//TODO: Links
			}
			getPluginLoader().disablePlugin(this);
			return;
		}
		
		plugin = this;
		getServer().getPluginManager().registerEvents(itemUpdater, this);
		getServer().getPluginManager().registerEvents(InventoryHandler.getInstance(), this);
		getServer().getPluginManager().registerEvents(activatorListener, this);
		getServer().getPluginManager().registerEvents(projectileListener, this);
		getServer().getPluginManager().registerEvents(recipeHandler, this);
		
		//Initial (re)load
		reload();
		
		//Register recipes
		recipeHandler.registerRecipes();
		
		getCommand(MainCommandExecutor.COMMAND_NAME).setExecutor(new MainCommandExecutor());
	}

	//Plugin disable
	@Override
	public void onDisable() {
		if (plugin == null) return;
		
		//Remove projectiles spawned by ProjectileAbility
		projectileListener.removeAbilityProjectiles();
		//Cancel timer checker
		if (timerCheckingTask != null) timerCheckingTask.cancel();
		//Set plugin to null
		plugin = null;
	}
	
	public void reload() {
		ConfigManager.getInstance().reload();
		itemUpdater.reload();
		InventoryHandler.getInstance().reload();
		
		//Timer checker
		if (timerCheckingTask != null) timerCheckingTask.cancel();
		timerCheckingTask = getServer().getScheduler().runTaskTimer(this, () -> {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				InventoryHandler.getInstance().getItemsFromPlayer(p).getItems().forEach(e ->e.grandItem.activateTimers(p));
			}
		}, 0L, ConfigManager.getInstance().getTimerHandlerDelay());
	}
	
}
