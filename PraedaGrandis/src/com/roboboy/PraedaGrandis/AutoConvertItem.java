package com.roboboy.PraedaGrandis;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.comphenix.attribute.NBTStorage;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;

public class AutoConvertItem
{
	public final GrandItem convertItem;
	
	public final String searchName;
	public final List<String> searchLore;
	public final Material searchType;
	
	public AutoConvertItem(GrandItem convertItem, String searchName, List<String> searchLore, Material searchType)
	{
		this.convertItem = convertItem;
		
		this.searchType = searchType;
		this.searchName = searchName.replace('&', '§');
		
		List<String> convertedSearchLore = new ArrayList<String>();
		for (String s : searchLore) {
			convertedSearchLore.add(s.replace('&', '§'));
		}
		this.searchLore = convertedSearchLore;
	}
	
	public boolean match(ItemStack item)
	{
		boolean matches = true;
		boolean meta = item.hasItemMeta();
		boolean lore = meta && item.getItemMeta().hasLore();
		
		if (searchType != null) matches = matches && item.getType().equals(searchType);
		if (searchName != null) matches = matches && meta && item.getItemMeta().getDisplayName().equals(searchName);
		for (String s : searchLore) {
			matches = matches && lore && item.getItemMeta().getLore().contains(s);
		}
		
		return matches && !(searchName == null && searchLore.isEmpty());
	}

	public ItemStack convert(ItemStack item)
	{
		NBTStorage storage = NBTStorage.newTarget(item, PraedaGrandis.STORAGE_ITEM_NAME);
		storage.setData(convertItem.id);
		
		return convertItem.update(storage.getTarget());
	}
}
