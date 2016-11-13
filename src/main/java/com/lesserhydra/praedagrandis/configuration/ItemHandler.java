package com.lesserhydra.praedagrandis.configuration;

import com.lesserhydra.praedagrandis.logging.GrandLogger;
import com.lesserhydra.praedagrandis.logging.LogType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads and stores all GrandItems
 * @author roboboy
 */
public class ItemHandler extends MultiConfig {
	
	private static ItemHandler instance = new ItemHandler();
	private ItemHandler() {}
	public static ItemHandler getInstance() {
		return instance;
	}
	
	private Map<String, GrandItem> items = new HashMap<String, GrandItem>();
	private List<AutoConvertItem> convertItems = new ArrayList<AutoConvertItem>();
	
	/**
	 * Reloads the item configuration files.<br>
	 * <br>
	 * <strong>The ConfigManager and AbilityHandler must be reloaded first.</strong>
	 */
	@Override
	public void reload() {
		//Cancel all running ability timers
		for (GrandItem item : items.values()) {
			item.stopTimers();
		}
		items.clear();
		convertItems.clear();
		super.reload(ConfigManager.getInstance().getItemFolder());
	}
	
	/**
	 * Loads GrandItems from a single configuration file
	 */
	@Override
	protected void loadConfig(FileConfiguration config)
	{
		for (String key : config.getKeys(false)) {
			ConfigurationSection itemConfig = config.getConfigurationSection(key);
			ConfigurationSection autoconvertSection = itemConfig.getConfigurationSection("autoconvert"); //TODO: Move to seperate file
			
			GrandItem item = new GrandItem(itemConfig);
			items.put(key.toLowerCase(), item);
			
			//autoconvert
			if (autoconvertSection != null)
			{
				//plugin.getLogger().info("Found autoconvert");
				String searchDisplay = itemConfig.getString("autoconvert.display", null);
				List<String> searchLore = itemConfig.getStringList("autoconvert.lore");
				Material searchType = Material.matchMaterial(itemConfig.getString("autoconvert.type", ""));
				convertItems.add(new AutoConvertItem(item, searchDisplay, searchLore, searchType));
			}
		}
	}
	
	public GrandItem getItem(String itemName) {
		return items.get(itemName.toLowerCase());
	}
	
	@Nullable
	public GrandItem matchItem(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) return null;
		String name = GrandItem.getItemName(item);
		return name == null ? null : items.get(name.toLowerCase());
	}

	public AutoConvertItem matchConvertItem(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) return null;
		for (AutoConvertItem convert : convertItems) {
			if (convert.match(item)) {
				GrandLogger.log("Converting item!", LogType.DEBUG); //TODO: Change to something more appropriate
				return convert;
			}
		}	
		return null;
	}

	public String listItems() {
		String result = "";
		for (String key : items.keySet()) {
			result += key + ", ";
		}
		return result.substring(0, result.length() - 2);
	}

	public Collection<String> getItemNames() {
		return items.keySet();
	}
	
}
