package com.roboboy.PraedaGrandis;

import com.google.common.collect.*;
import com.roboboy.PraedaGrandis.Abilities.*;
import com.roboboy.PraedaGrandis.Configuration.*;
import java.util.*;

public class GrandInventory
{
	private Map<String, EnumMultiset<ItemSlotType>> invMap = new HashMap<>();
	
	public void addItem(GrandItem gItem, ItemSlotType slotType) {
		if (!invMap.containsKey(gItem.getId())) {
			invMap.put(gItem.getId(), EnumMultiset.create(ItemSlotType.class));
		}
		invMap.get(gItem.getId()).add(slotType);
	}
	
	public void removeItem(GrandItem gItem, ItemSlotType slotType) {
		if (!invMap.containsKey(gItem.getId())) return;
		invMap.get(gItem.getId()).remove((Object)slotType);
	}
	
	public boolean contains(GrandItem gItem) {
		return (invMap.containsKey(gItem.getId()) && !invMap.get(gItem.getId()).isEmpty());
	}
	
	public boolean containsInSlotType(GrandItem gItem, ItemSlotType slotType) {
		return (invMap.containsKey(gItem.getId()) && invMap.get(gItem.getId()).contains(slotType));
	}
	
	public Set<GrandItem> getItems() {
		Set<GrandItem> results = new HashSet<GrandItem>();
		for (String itemID : invMap.keySet()) {
			results.add(PraedaGrandis.plugin.itemHandler.getItem(itemID));
		}
		return results;
	}
	
	public EnumSet<ItemSlotType> getSlotTypes(GrandItem gItem) {
		if (!invMap.containsKey(gItem.getId())) return EnumSet.noneOf(ItemSlotType.class);
		return EnumSet.copyOf(invMap.get(gItem.getId()).elementSet());
	}
}
