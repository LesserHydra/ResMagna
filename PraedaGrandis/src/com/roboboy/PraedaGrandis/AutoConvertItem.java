package com.roboboy.PraedaGrandis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
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
		this.searchName = searchName.replace('&', ChatColor.COLOR_CHAR);
		
		List<String> convertedSearchLore = new ArrayList<String>();
		for (String s : searchLore) {
			convertedSearchLore.add(s.replace('&', ChatColor.COLOR_CHAR));
		}
		this.searchLore = convertedSearchLore;
	}
	
	public boolean match(ItemStack item)
	{
		boolean matches = true;
		boolean hasMeta = item.hasItemMeta();
		boolean hasLore = hasMeta && item.getItemMeta().hasLore();
		boolean hasDisplay = hasMeta && item.getItemMeta().hasDisplayName();
		
		if (searchType != null) matches = matches && item.getType().equals(searchType);
		if (searchName != null) matches = matches && hasDisplay && item.getItemMeta().getDisplayName().equals(searchName);
		for (String s : searchLore) {
			matches = matches && hasLore && item.getItemMeta().getLore().contains(s);
		}
		
		return matches && !(searchName == null && searchLore.isEmpty());
	}

	public ItemStack convert(ItemStack item)
	{
		NBTStorage storage = NBTStorage.newTarget(item, PraedaGrandis.STORAGE_ITEM_NAME);
		storage.setData(convertItem.id);
		
		UUID newID = UUID.randomUUID();
		storage = NBTStorage.newTarget(storage.getTarget(), PraedaGrandis.STORAGE_ITEM_ID);
		storage.setData(newID.toString());
		
		return convertItem.update(storage.getTarget());
	}
}
