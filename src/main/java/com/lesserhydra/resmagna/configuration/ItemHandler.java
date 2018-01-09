package com.lesserhydra.resmagna.configuration;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.lesserhydra.resmagna.ResMagna;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads and stores all GrandItems
 * @author roboboy
 */
public class ItemHandler extends MultiConfig {
	
	private static ItemHandler instance = new ItemHandler();
	private ItemHandler() {}
	public static ItemHandler getInstance() { return instance; }
	
	private Map<String, GrandItem> items = new HashMap<>();
	private Multimap<Material, AutoConvertItem> convertItems = LinkedListMultimap.create();
	
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
	protected void loadConfig(FileConfiguration config) {
		for (String key : config.getKeys(false)) {
			ConfigurationSection itemConfig = config.getConfigurationSection(key);
			
			GrandItem item = new GrandItem(itemConfig);
			items.put(key.toLowerCase(), item);
			
			//autoconvert
			ConfigurationSection autoconvertSection = itemConfig.getConfigurationSection("autoconvert"); //TODO: Move to seperate file
			if (autoconvertSection != null) {
				GrandLogger.log("Found autoconvert for " + item.getName(), LogType.CONFIG_PARSING);
				Material searchType = Material.matchMaterial(itemConfig.getString("autoconvert.type"));
				String searchDisplay = itemConfig.getString("autoconvert.display", null);
				List<String> searchLore = itemConfig.getStringList("autoconvert.lore");
				convertItems.put(searchType, new AutoConvertItem(item, searchDisplay, searchLore, searchType));
			}
		}
	}
	
	@Nullable
	public GrandItem getItem(String itemName) {
		return items.get(itemName.toLowerCase());
	}
	
	@Nullable
	public GrandItem matchItem(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) return null;
		String name = GrandItem.getItemName(item);
		return name == null ? null : items.get(name.toLowerCase());
	}
	
	@Nullable
	public AutoConvertItem matchConvertItem(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) return null;
		for (AutoConvertItem convert : convertItems.get(item.getType())) {
			if (convert.match(item)) {
				GrandLogger.log("Converting " + item.getType() + " to "
						+ convert.getGrandItem().getName() , LogType.DEBUG);
				return convert;
			}
		}	
		return null;
	}

	public Collection<String> getItemNames() {
		return items.keySet();
	}
	
	public String listItems() {
		return getItemNames().stream()
				.collect(Collectors.joining(", "));
	}
	
	public Stream<Recipe> streamRecipes() {
		return items.values().stream()
				.map(GrandItem::getRecipe)
				.filter(Objects::nonNull);
	}

}
