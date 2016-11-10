package com.roboboy.PraedaGrandis.Conditions;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.GrandInventory;
import com.roboboy.PraedaGrandis.InventoryHandler;
import com.roboboy.PraedaGrandis.Arguments.ItemSlotType;

class IsHolding implements Condition {
	
	final private String itemName;

	IsHolding(ArgumentBlock args) {
		//TODO: Error handling/logging
		this.itemName = args.getString(true, "",	"name", "n", null);
	}

	@Override
	public boolean test(Target target) {
		if (!target.isPlayer()) return false;
		GrandInventory gInv = InventoryHandler.getInstance().getItemsFromPlayer(target.asPlayer());
		for (GrandInventory.InventoryElement element : gInv.getItems(itemName)) {
			if (element.slotType == ItemSlotType.HELD) return true;
		}
		return false;
	}

}
