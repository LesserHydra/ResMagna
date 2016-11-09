package com.roboboy.PraedaGrandis.Abilities.Conditions;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.GrandInventory;
import com.roboboy.PraedaGrandis.InventoryHandler;
import com.roboboy.PraedaGrandis.ItemSlotType;

class IsHolding implements Condition {
	
	final private String itemName;

	IsHolding(BlockArguments args) {
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
