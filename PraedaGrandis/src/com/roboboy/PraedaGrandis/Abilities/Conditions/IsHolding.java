package com.roboboy.PraedaGrandis.Abilities.Conditions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.comphenix.attribute.NBTStorage;
import com.roboboy.PraedaGrandis.PraedaGrandis;
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
	protected boolean checkThis(Target target)
	{
		if (target.get() instanceof Player)
		{
			ItemStack item = ((Player) target.get()).getItemInHand();
			if (item != null && item.getType() != Material.AIR) {
				String id = NBTStorage.newTarget(item, PraedaGrandis.STORAGE_ITEM_NAME).getString("").toLowerCase();
				if (id.toLowerCase().equals(itemName)) return true;
			}
		}
		return false;
	}

}
