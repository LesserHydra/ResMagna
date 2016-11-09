package com.roboboy.PraedaGrandis.Conditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.roboboy.PraedaGrandis.GrandInventory;
import com.roboboy.PraedaGrandis.InventoryHandler;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class IsWearing implements Condition {
	
	private final List<String> itemNames = new ArrayList<>();

	IsWearing(BlockArguments args) {
		//TODO: Error handling/logging
		String namesString = args.getString(false, "", "names", "name", "n", null);
		Collections.addAll(itemNames,
				namesString.replace("(", "").replace(")", "").replace(",", "").split(" ")
		);
	}

	@Override
	public boolean test(Target target) {
		if (!target.isPlayer()) return false;
		GrandInventory gInv = InventoryHandler.getInstance().getItemsFromPlayer(target.asPlayer());
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
