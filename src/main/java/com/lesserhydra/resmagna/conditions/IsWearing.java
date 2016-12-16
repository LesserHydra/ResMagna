package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.GrandInventory;
import com.lesserhydra.resmagna.InventoryHandler;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.arguments.ItemSlotType;
import com.lesserhydra.resmagna.targeters.Target;

import java.util.Arrays;
import java.util.List;

class IsWearing implements Condition {
	
	private final Evaluators.ForString namesString;

	IsWearing(ArgumentBlock args) {
		//TODO: Error handling/logging
		this.namesString = args.getString(false, "",      "names", "name", "n", null);
	}

	@Override
	public boolean test(Target target) {
		if (!target.isPlayer()) return false;
		if (!namesString.evaluate(target)) return false;
		
		List<String> itemNames = Arrays.asList(namesString.get().toLowerCase().replaceAll("\\(|\\)|,", "").split("\\s+"));
		GrandInventory gInv = InventoryHandler.getInstance().getItemsFromPlayer(target.asPlayer());
		return itemNames.stream()
				.allMatch(name -> gInv.getItems(name).stream()
						.anyMatch(element -> element.slotType.isSubtypeOf(ItemSlotType.WORN)));
	}
	
}
