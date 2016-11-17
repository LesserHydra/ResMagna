package com.lesserhydra.praedagrandis.configuration;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AutoConvertItem {
	
	private final GrandItem convertItem;
	
	private final String searchName;
	private final List<String> searchLore;
	private final Material searchType;
	
	AutoConvertItem(GrandItem convertItem, String searchName, List<String> searchLore, Material searchType) {
		this.convertItem = convertItem;
		
		this.searchType = searchType;
		this.searchName = searchName.replace('&', ChatColor.COLOR_CHAR);
		
		List<String> convertedSearchLore = new ArrayList<>();
		for (String s : searchLore) {
			convertedSearchLore.add(s.replace('&', ChatColor.COLOR_CHAR));
		}
		this.searchLore = convertedSearchLore;
	}
	
	public boolean match(ItemStack item) {
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
	
	@NotNull
	public ItemStack convert(ItemStack item) {
		ItemStack result = convertItem.markItem(item);
		result = convertItem.update(result);
		
		//Yes compiler, I KNOW update() is nullable. Just trust me when I say it will NOT return null.
		//Could technically just return original result, but this way is more correct and less likely to be broken by
		//silly mistakes in the future.
		assert result != null;
		return result;
	}
	
}
