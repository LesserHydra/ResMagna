package com.roboboy.PraedaGrandis;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.roboboy.PraedaGrandis.Configuration.ConfigManager;
import com.roboboy.PraedaGrandis.Configuration.GrandAbilityHandler;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;


public class PraedaGrandis extends JavaPlugin
{
	public static PraedaGrandis plugin;
	public static final String STORAGE_ITEM_NAME = "PraedaGrandis.GrandItem";
	public static final String STORAGE_CUSTOM_VAR = "PraedaGrandis.Variable.";
	//public static final UUID ID = UUID.fromString("2b56453f-6eec-4313-8424-4d5b6c456c70");
	
	//private Map<String, GrandItem> items = new HashMap<String, GrandItem>();
	
	public ConfigManager configManager = new ConfigManager(this);
	public ItemUpdater itemUpdater = new ItemUpdater(this);
	public ItemHandler itemHandler;// = new ItemHandler(this);
	public GrandAbilityHandler abilityHandler;
	
	//private int timerTaskNum;
	
	//Plugin startup
	@Override
	public void onEnable()
	{
		plugin = this;
		getServer().getPluginManager().registerEvents(itemUpdater, this);
		getServer().getPluginManager().registerEvents(new ActivatorListener(this), this);
		
		abilityHandler = new GrandAbilityHandler(this, configManager.abilityFolder);
		itemHandler = new ItemHandler(this, configManager.itemFolder);
		reload();
		
		//Timer handler
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
            	for (Player p : getServer().getOnlinePlayers()) {
            		for (GrandItem item : itemHandler.getItemsFromPlayer(p).keySet()) {
            			item.activateTimers(p);
            		}
            	}
            }
        }, 0L, 80L); //TODO: Make this configurable
	}

	//Plugin disable
	@Override
	public void onDisable()
	{
		//Cancel timer handler
		getServer().getScheduler().cancelTasks(this);
		plugin = null;
	}
	
	private void reload()
	{
		//HandlerList.unregisterAll(this);
		configManager = new ConfigManager(this);
		itemUpdater.reload();
		abilityHandler.reload();
		itemHandler.reload();
	}
	
	//TODO: Commands
	//		- reload	(Given)
	//		- test		(Various test functions)
	//		- check		(Check if item in hand is registered)
	//		- list		(List all registered GrandItems)
	//		- give		(Analog to vanilla command)
	//Command handling
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player)sender;
			if (args.length > 0)
			{
				String subCommand = args[0].toLowerCase();
				switch (subCommand)
				{
				case "list":
					player.sendMessage(ChatColor.BLUE + itemHandler.listItems());
					break;
				case "check":
					if (player.getItemInHand().getType() != Material.AIR)
					{
						if (itemHandler.matchItem(player.getItemInHand()) != null)
							sender.sendMessage(ChatColor.BLUE + "Match!");
						else
							sender.sendMessage(ChatColor.BLUE + "No match found.");
					}
					else sender.sendMessage(ChatColor.RED + "Nothing in hand!");
					break;
				case "give":
					if (args.length > 2)
					{
						Player givePlayer = player;//getServer().getPlayer(args[1]);
						GrandItem giveItem = itemHandler.getItem(args[2]);
						if (givePlayer != null && giveItem != null)
							givePlayer.getInventory().addItem(giveItem.create());
						else
							sender.sendMessage(ChatColor.RED + "Item or player does not exist.");
					}
					else showCommandHelp(sender, cmd, subCommand);
					break;
				/*case "test":
					//TODO: test
					if (args.length == 3)
					{
						ItemStack item = player.getItemInHand();
						Attributes testAtts = new Attributes(item);
						Builder build = Attribute.newBuilder().type(AttributeType.GENERIC_MOVEMENT_SPEED).name("generic.movementSpeed").amount(Double.parseDouble(args[1]));
						Attribute att = build.build();
						testAtts.add(att);
						player.setItemInHand(testAtts.getStack());
					}
					else showCommandHelp(sender, cmd, subCommand);
					break;
				*/
				case "reload":
					reload();
					sender.sendMessage(ChatColor.GREEN + "Reloaded config files.");
					break;
				default:
					showCommandHelp(sender, cmd, subCommand);
					break;
				}
			}
			else showCommandHelp(sender, cmd, "");
		}
		else sender.sendMessage(ChatColor.RED + "Only players may use this command!");
		
		return true;
	}
	
	private void showCommandHelp(CommandSender sender, Command cmd, String subCommand)
	{
		switch (subCommand.toLowerCase())
		{
		case "give":
			//TODO: give
			sender.sendMessage(ChatColor.RED + "*INSERT HELP HERE*");
			break;
		case "test":
			//TODO: test
			sender.sendMessage(ChatColor.RED + "*INSERT HELP HERE*");
			break;
		case "reload":
			//TODO: reload
			sender.sendMessage(ChatColor.BLUE + "Reloads the plugin. ");
			break;
		default:
			sender.sendMessage(ChatColor.GRAY + "---------------PraedaGrandis---------------");
			sender.sendMessage(ChatColor.GRAY + "Insert amazing description here");
			sender.sendMessage(ChatColor.GRAY + "/pg give	- Analog to vanilla command");
			sender.sendMessage(ChatColor.GRAY + "/pg test	- Various test functions");
			sender.sendMessage(ChatColor.GRAY + "/pg reload");
			break;
		}
	}
	
	//TODO: Expand logging system
	static public void log(String s, LogType type) {
		plugin.getLogger().info(s);
	}
	
	/* How will these items be created? It needs to be in some way that another plugin
	 * could access. For example, using an item's name or lore. Perhaps an item that gets
	 * turned into the loot when held by a player? Maybe it need to be interacted with?*/
}
