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
		this.itemName = args.getString(true, "",	"name", "n", null);
	}

	@Override
	protected boolean checkThis(Target target) {
		if (!(target.getEntity() instanceof Player)) return false;
		
		GrandInventory gInv = InventoryHandler.getInstance().getItemsFromPlayer((Player)target.getEntity());
		for (GrandInventory.InventoryElement element : gInv.getItems(itemName)) {
			if (element.slotType == ItemSlotType.HELD) return true;
		}
		return false;
	}

}
