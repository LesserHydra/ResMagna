package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

class DisarmAbility implements Ability {
	
	private final boolean mainHand;
	private final boolean offHand;
	private final boolean helmet;
	private final boolean chestplate;
	private final boolean leggings;
	private final boolean boots;
	
	DisarmAbility(BlockArguments args) {
		mainHand = args.getBoolean(false, false,	"mainhand", "heldright", "right", "main");
		offHand = args.getBoolean(false, false,		"offhand", "heldleft", "shield", "left", "off");
		helmet = args.getBoolean(false, false,		"helmet", "helm", "hlm");
		chestplate = args.getBoolean(false, false,	"chestplate", "chest", "cst");
		leggings = args.getBoolean(false, false, 	"leggings", "legs", "lgs");
		boots = args.getBoolean(false, false,		"boots", "shoes", "bts");
	}

	@Override
	public void execute(Target target) {
		LivingEntity targetEntity = target.getEntity();
		if (targetEntity == null) return;
		
		Location entityLocation = target.getLocation();
		EntityEquipment equipment = targetEntity.getEquipment();
		
		if (mainHand) {
			entityLocation.getWorld().dropItemNaturally(entityLocation, equipment.getItemInMainHand());
			equipment.setItemInMainHand(null);
		}
		
		if (offHand) {
			entityLocation.getWorld().dropItemNaturally(entityLocation, equipment.getItemInOffHand());
			equipment.setItemInOffHand(null);
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
