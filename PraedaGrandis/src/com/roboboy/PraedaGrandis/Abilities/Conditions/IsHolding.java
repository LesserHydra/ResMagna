package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.GrandInventory;
import com.roboboy.PraedaGrandis.InventoryHandler;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class IsHolding extends Condition
{
	final private String itemName;

	public IsHolding(Targeter targeter, boolean not, BlockArguments args) {
		super(targeter, not);
		
		//TODO: Error handling/logging
		String itemName = args.get(null, "", false);
		this.itemName = args.get("name", itemName, false);
	}

	@Override
	protected boolean checkThis(Target target) {
		if (!(target.get() instanceof Player)) return false;
		
		GrandInventory gInv = InventoryHandler.getInstance().getItemsFromPlayer((Player)target.get());
		for (GrandInventory.InventoryElement element : gInv.getItems(itemName)) {
			if (element.slotType == ItemSlotType.HELD) return true;
		}
		return false;
	}

}
