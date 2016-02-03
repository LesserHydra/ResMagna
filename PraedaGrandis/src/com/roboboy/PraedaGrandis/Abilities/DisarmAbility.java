package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class DisarmAbility extends Ability
{
	private final boolean held;
	private final boolean helmet;
	private final boolean chestplate;
	private final boolean leggings;
	private final boolean boots;
	
	public DisarmAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		
		held = args.getBoolean("held", false, false);
		helmet = args.getBoolean("helmet", false, false);
		chestplate = args.getBoolean("chestplate", false, false);
		leggings = args.getBoolean("leggings", false, false);
		boots = args.getBoolean("boots", false, false);
	}

	@Override
	protected void execute(Target target)
	{
		Location entityLocation = target.get().getLocation();
		EntityEquipment equipment = target.get().getEquipment();
		
		if (held) {
			entityLocation.getWorld().dropItemNaturally(entityLocation, equipment.getItemInHand());
			equipment.setItemInHand(null);
		}
		
		if (helmet) {
			entityLocation.getWorld().dropItemNaturally(entityLocation, equipment.getHelmet());
			equipment.setHelmet(null);
		}
		
		if (chestplate) {
			entityLocation.getWorld().dropItemNaturally(entityLocation, equipment.getChestplate());
			equipment.setChestplate(null);
		}
		
		if (leggings) {
			entityLocation.getWorld().dropItemNaturally(entityLocation, equipment.getLeggings());
			equipment.setLeggings(null);
		}
		
		if (boots) {
			entityLocation.getWorld().dropItemNaturally(entityLocation, equipment.getBoots());
			equipment.setBoots(null);
		}
	}

}
