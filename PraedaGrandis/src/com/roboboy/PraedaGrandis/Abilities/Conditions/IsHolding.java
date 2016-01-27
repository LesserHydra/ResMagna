package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.GrandInventory;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

public class IsHolding extends Condition
{
	final private String itemName;

	protected IsHolding(Targeter targeter, boolean not, BlockArguments args) {
		super(targeter, not);
		//TODO: Error handling/logging
		itemName = args.get("name", "", true);
	}

	@Override
	protected boolean checkThis(Target target) {
		if (!(target.get() instanceof Player)) return false;
		
		GrandInventory gInv = PraedaGrandis.plugin.inventoryHandler.getItemsFromPlayer((Player)target.get());
		for (GrandInventory.InventoryElement element : gInv.getItems(itemName)) {
			if (element.slotType == ItemSlotType.HELD) return true;
		}
		return false;
	}

}
