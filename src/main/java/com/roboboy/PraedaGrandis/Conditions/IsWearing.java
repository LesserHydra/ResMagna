package com.roboboy.PraedaGrandis.Conditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.roboboy.PraedaGrandis.GrandInventory;
import com.roboboy.PraedaGrandis.InventoryHandler;
import com.roboboy.PraedaGrandis.Arguments.ItemSlotType;
import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import org.bukkit.entity.Player;

class IsWearing implements Condition.ForPlayer {
	
	private final List<String> itemNames = new ArrayList<>();

	IsWearing(ArgumentBlock args) {
		//TODO: Error handling/logging
		String namesString = args.getString(false, "", "names", "name", "n", null);
		Collections.addAll(itemNames,
				namesString.replace("(", "").replace(")", "").replace(",", "").split(" ")
		);
	}

	@Override
	public boolean test(Player target) {
		GrandInventory gInv = InventoryHandler.getInstance().getItemsFromPlayer(target);
		for (String name : itemNames) {
			if (!found(gInv.getItems(name))) return false;
		}
		return true;
	}
	
	private boolean found(List<GrandInventory.InventoryElement> elementList) {
		for (GrandInventory.InventoryElement element : elementList) {
			if (!element.slotType.isSubtypeOf(ItemSlotType.WORN)) return true;
		}
		return false;
	}

}
