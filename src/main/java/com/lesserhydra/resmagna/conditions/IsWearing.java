package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.GrandInventory;
import com.lesserhydra.resmagna.InventoryHandler;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.ItemSlotType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

class IsWearing implements Condition.ForPlayer {
	
	private final List<String> itemNames;

	IsWearing(ArgumentBlock args) {
		//TODO: Error handling/logging
		String namesString = args.getString(false, "",      "names", "name", "n", null);
		itemNames = Arrays.asList(namesString.toLowerCase().replaceAll("\\(|\\)|,", "").split("\\s+"));
	}

	@Override
	public boolean test(Player target) {
		GrandInventory gInv = InventoryHandler.getInstance().getItemsFromPlayer(target);
		return itemNames.stream()
				.allMatch(name -> gInv.getItems(name).stream()
						.anyMatch(element -> element.slotType.isSubtypeOf(ItemSlotType.WORN)));
	}
	
}
