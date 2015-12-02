package com.roboboy.PraedaGrandis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.comphenix.attribute.NBTStorage;
import com.roboboy.PraedaGrandis.Abilities.ItemSlotType;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;
import com.roboboy.PraedaGrandis.Configuration.MultiConfig;

public class ItemHandler extends MultiConfig
{
	private Map<String, GrandItem> items = new HashMap<String, GrandItem>();
	private List<AutoConvertItem> convertItems = new ArrayList<AutoConvertItem>();
	
	public ItemHandler(PraedaGrandis p, File folder) {
		super(p, folder);
	}
	
	@Override
	public void reload()
	{
		items.clear();
		convertItems.clear();
		super.reload();
	}
	
	@Override
	protected void loadConfig(FileConfiguration config)
	{
		for (String key : config.getKeys(false))
		{
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

	public List<GrandItem> getItemsFromPlayer(Player p, ItemSlotType slotType)
	{
		List<GrandItem> results = new LinkedList<>();
		
		for (ItemStack item: slotType.getItems(p)) {
			GrandItem gItem = matchItem(item);
			if (gItem != null) results.add(gItem);
		}
		
		return results;
	}
	
	public GrandItem getItem(String itemName) {
		return items.get(itemName.toLowerCase());
	}
	
	public GrandItem matchItem(ItemStack item)
	{
		if (item == null || item.getType() == Material.AIR) return null;
		String id = NBTStorage.newTarget(item, PraedaGrandis.STORAGE_ITEM_NAME).getString("");//.toLowerCase();
		return items.get(id.toLowerCase());
	}

	public AutoConvertItem matchConvertItem(ItemStack item)
	{
		if (item == null || item.getType() == Material.AIR) return null;
		for (AutoConvertItem convert : convertItems) {
			if (convert.match(item)) {
				plugin.getLogger().info("Converting item!");
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

	public Set<String> getItemNames() {
		return items.keySet();
	}
}
