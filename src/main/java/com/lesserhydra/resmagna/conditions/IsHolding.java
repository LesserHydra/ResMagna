package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.GrandInventory;
import com.lesserhydra.resmagna.InventoryHandler;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.ItemSlotType;
import org.bukkit.entity.Player;

class IsHolding implements Condition.ForPlayer {
	
	private final String itemName;

	IsHolding(ArgumentBlock args) {
		//TODO: Error handling/logging
		this.itemName = args.getString(true, "",	"name", "n", null)
				.toLowerCase();
	}

	@Override
	public boolean test(Player target) {
		GrandInventory gInv = InventoryHandler.getInstance().getItemsFromPlayer(target);
		for (GrandInventory.InventoryElement element : gInv.getItems(itemName)) {
			if (element.slotType.isSubtypeOf(ItemSlotType.HELD)) return true;
		}
		return false;
	}

}
