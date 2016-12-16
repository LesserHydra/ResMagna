package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.GrandInventory;
import com.lesserhydra.resmagna.InventoryHandler;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.arguments.ItemSlotType;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.entity.Player;

class IsHolding implements Condition {
	
	private final Evaluators.ForString itemName;

	IsHolding(ArgumentBlock args) {
		this.itemName = args.getString(true, "",	"name", "n", null);
	}

	@Override
	public boolean test(Target target) {
		if (!target.isPlayer()) return false;
		
		if (!itemName.evaluate(target)) return false;
		
		GrandInventory gInv = InventoryHandler.getInstance().getItemsFromPlayer(target.asPlayer());
		return gInv.getItems(itemName.get().toLowerCase()).stream()
				.anyMatch(element -> element.slotType.isSubtypeOf(ItemSlotType.HELD));
	}

}
