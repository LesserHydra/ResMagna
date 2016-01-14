package com.roboboy.PraedaGrandis;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.roboboy.PraedaGrandis.Commands.MainCommandExecutor;
import com.roboboy.PraedaGrandis.Configuration.ConfigManager;
import com.roboboy.PraedaGrandis.Configuration.GrandAbilityHandler;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;


public class PraedaGrandis extends JavaPlugin
{
	public static PraedaGrandis plugin;
	public static final String STORAGE_ITEM_NAME = "PraedaGrandis.GrandItemName";
	public static final String STORAGE_ITEM_ID = "PraedaGrandis.GrandItemID";
	//public static final UUID ID = UUID.fromString("2b56453f-6eec-4313-8424-4d5b6c456c70");
	
	public final ConfigManager configManager = new ConfigManager(this);
	public final GrandAbilityHandler abilityHandler = new GrandAbilityHandler(this);
	public final ItemHandler itemHandler = new ItemHandler(this);
	public final ItemUpdater itemUpdater = new ItemUpdater(this);
	public final InventoryHandler inventoryHandler = new InventoryHandler(this);
	public final ActivatorListener activatorListener = new ActivatorListener(this);
	
	private long timerHandlerDelay = 80L;
	
	//Plugin startup
	@Override
	public void onEnable()
	{
		plugin = this;
		getServer().getPluginManager().registerEvents(itemUpdater, this);
		getServer().getPluginManager().registerEvents(inventoryHandler, this);
		getServer().getPluginManager().registerEvents(activatorListener, this);
		
		reload();
		
		getCommand(MainCommandExecutor.COMMAND_NAME).setExecutor(new MainCommandExecutor());
		
		//Timer handler
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
            	for (Player p : getServer().getOnlinePlayers()) {
            		for (GrandItem item : inventoryHandler.getItemsFromPlayer(p).getItems()) {
            			item.activateTimers(p);
            		}
            	}
            }
        }, 0L, timerHandlerDelay);
	}

	//Plugin disable
	@Override
	public void onDisable()
	{
		//Cancel timer handler
		getServer().getScheduler().cancelTasks(this);
		plugin = null;
	}
	
	public void reload() {
		configManager.reload();
		abilityHandler.reload();
		itemHandler.reload();
		itemUpdater.reload();
		inventoryHandler.reload();
	}
	
	//TODO: Expand logging system
	static public void log(String s, LogType type) {
		plugin.getLogger().info(s);
	}
}
