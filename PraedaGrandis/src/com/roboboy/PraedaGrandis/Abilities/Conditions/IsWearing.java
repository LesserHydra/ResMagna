package com.roboboy.PraedaGrandis.Abilities.Conditions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.comphenix.attribute.NBTStorage;
import com.roboboy.PraedaGrandis.PraedaGrandis;
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
		if (target.get() instanceof Player)
		{
			Set<String> toMatch = new HashSet<>(itemNames);
			for (ItemStack item : ((Player) target.get()).getInventory().getArmorContents()) {
				if (item != null && item.getType() != Material.AIR) {
					String id = NBTStorage.newTarget(item, PraedaGrandis.STORAGE_ITEM_NAME).getString("").toLowerCase();
					toMatch.remove(id);
				}
			}
			if (toMatch.isEmpty()) return true;
		}
		return false;
	}

}
