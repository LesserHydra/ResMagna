package com.roboboy.PraedaGrandis;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.roboboy.PraedaGrandis.Commands.MainCommandExecutor;
import com.roboboy.PraedaGrandis.Configuration.ConfigManager;
import com.roboboy.PraedaGrandis.Configuration.GrandAbilityHandler;

public class PraedaGrandis extends JavaPlugin
{
	public static PraedaGrandis plugin;
	public static final String STORAGE_ITEM_NAME = "PraedaGrandis.GrandItemName";
	public static final String STORAGE_ITEM_ID = "PraedaGrandis.GrandItemID";
	//public static final UUID ID = UUID.fromString("2b56453f-6eec-4313-8424-4d5b6c456c70");
	
	public ConfigManager configManager = new ConfigManager(this);
	public ItemUpdater itemUpdater = new ItemUpdater(this);
	public InventoryHandler inventoryHandler = new InventoryHandler(this);
	public ActivatorListener activatorListener = new ActivatorListener(this);
	public ItemHandler itemHandler;
	public GrandAbilityHandler abilityHandler;
	
	private long timerHandlerDelay = 80L;
	
	//Plugin startup
	@Override
	public void onEnable()
	{
		plugin = this;
		getServer().getPluginManager().registerEvents(itemUpdater, this);
		getServer().getPluginManager().registerEvents(inventoryHandler, this);
		getServer().getPluginManager().registerEvents(activatorListener, this);
		
		abilityHandler = new GrandAbilityHandler(this, configManager.abilityFolder);
		itemHandler = new ItemHandler(this, configManager.itemFolder);
		reload();
		
		getCommand(MainCommandExecutor.COMMAND_NAME).setExecutor(new MainCommandExecutor());
		
		//Timer handler
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
            	for (Player p : getServer().getOnlinePlayers()) {
            		for (GrandInventory.InventoryElement element : inventoryHandler.getItemsFromPlayer(p).getItems()) {
            			element.grandItem.activateTimers(p);
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
	
	public void reload()
	{
		//HandlerList.unregisterAll(this);
		configManager = new ConfigManager(this);
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
