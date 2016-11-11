package com.lesserhydra.praedagrandis.conditions;

import com.lesserhydra.praedagrandis.arguments.ArgumentBlock;
import com.lesserhydra.praedagrandis.GrandInventory;
import com.lesserhydra.praedagrandis.InventoryHandler;
import com.lesserhydra.praedagrandis.arguments.ItemSlotType;
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
