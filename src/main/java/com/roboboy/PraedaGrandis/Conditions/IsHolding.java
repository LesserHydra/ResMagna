package com.roboboy.PraedaGrandis.Conditions;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import com.roboboy.PraedaGrandis.GrandInventory;
import com.roboboy.PraedaGrandis.InventoryHandler;
import com.roboboy.PraedaGrandis.Arguments.ItemSlotType;
import org.bukkit.entity.Player;

class IsHolding implements Condition.ForPlayer {
	
	final private String itemName;

	IsHolding(ArgumentBlock args) {
		//TODO: Error handling/logging
		this.itemName = args.getString(true, "",	"name", "n", null);
	}

	@Override
	public boolean test(Player target) {
		GrandInventory gInv = InventoryHandler.getInstance().getItemsFromPlayer(target);
		for (GrandInventory.InventoryElement element : gInv.getItems(itemName)) {
			if (element.slotType == ItemSlotType.HELD) return true;
		}
		return false;
	}

}
