package com.roboboy.PraedaGrandis.Abilities.Conditions;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.GrandInventory;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

public class IsWearing extends Condition
{
	final private List<String> itemNames = new ArrayList<>();

	protected IsWearing(Targeter targeter, boolean not, BlockArguments args)
	{
		super(targeter, not);
		//TODO: Error handling/logging
		for (String s : args.get("names", "", true).replace("(", "").replace(")", "").replace(",", "").split(" ")) {
			itemNames.add(s);
		}
	}

	@Override
	protected boolean checkThis(Target target)
	{
		if (!(target.get() instanceof Player)) return false;
		
		GrandInventory gInv = PraedaGrandis.plugin.inventoryHandler.getItemsFromPlayer((Player)target.get());
		for (String name : itemNames) {
			if (!found(gInv.getItems(name))) return false;
		}
		return true;
	}
	
	private boolean found(List<GrandInventory.InventoryElement> elementList) {
		for (GrandInventory.InventoryElement element : elementList) {
			if (!element.slotType.isSubtypeOf(ItemSlotType.WORN)) return true;
		}
		return false;
	}

}
