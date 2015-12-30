package com.roboboy.PraedaGrandis.Abilities.Conditions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.GrandInventory;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;

public class IsWearing extends Condition
{
	final private List<String> itemNames = new ArrayList<>();

	protected IsWearing(Targeter targeter, boolean not, ConfigString args)
	{
		super(targeter, not);
		for (String s : args.get(1).replace("(", "").replace(")", "").replace(",", "").split(" ")) {
			itemNames.add(s);
		}
	}

	@Override
	protected boolean checkThis(Target target)
	{
		if (!(target.get() instanceof Player)) return false;
		Set<String> toMatch = new HashSet<>(itemNames);
		GrandInventory gInv = PraedaGrandis.plugin.inventoryHandler.getItemsFromPlayer((Player)target.get());
		for (GrandInventory.InventoryElement element : gInv.getItems()) {
			if (element.slotType.isSubtypeOf(ItemSlotType.WORN)) {
				toMatch.remove(element.grandItem.getName());
			}
		}
		return toMatch.isEmpty();
	}

}
