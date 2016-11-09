package com.roboboy.PraedaGrandis.Abilities.Conditions;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.GrandInventory;
import com.roboboy.PraedaGrandis.InventoryHandler;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class IsWearing extends Condition
{
	private final List<String> itemNames = new ArrayList<>();

	protected IsWearing(Targeter targeter, boolean not, BlockArguments args)
	{
		super(targeter, not);
		
		//TODO: Error handling/logging
		String namesString = args.getString(false, "", "names", "name", "n", null);
		for (String s : namesString.replace("(", "").replace(")", "").replace(",", "").split(" ")) {
			itemNames.add(s);
		}
	}

	@Override
	protected boolean checkThis(Target target)
	{
		if (!(target.getEntity() instanceof Player)) return false;
		
		GrandInventory gInv = InventoryHandler.getInstance().getItemsFromPlayer((Player)target.getEntity());
		return gInv.containsAll(itemNames, ItemSlotType.WORN);
	}
	
}
