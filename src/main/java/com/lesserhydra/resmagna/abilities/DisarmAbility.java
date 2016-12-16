package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

class DisarmAbility implements Ability {
	
	private final Evaluators.ForBoolean mainHand;
	private final Evaluators.ForBoolean offHand;
	private final Evaluators.ForBoolean helmet;
	private final Evaluators.ForBoolean chestplate;
	private final Evaluators.ForBoolean leggings;
	private final Evaluators.ForBoolean boots;
	
	DisarmAbility(ArgumentBlock args) {
		mainHand = args.getBoolean(false, false,	"mainhand", "heldright", "right", "main");
		offHand = args.getBoolean(false, false,		"offhand", "heldleft", "shield", "left", "off");
		helmet = args.getBoolean(false, false,		"helmet", "helm", "hlm");
		chestplate = args.getBoolean(false, false,	"chestplate", "chest", "cst");
		leggings = args.getBoolean(false, false, 	"leggings", "legs", "lgs");
		boots = args.getBoolean(false, false,		"boots", "shoes", "bts");
	}

	@Override
	public void run(Target target) {
		if (!target.isEntity()) {
			GrandLogger.log("Tried to run disarm ability with invalid target.", LogType.RUNTIME_ERRORS);
			return;
		}
		
		if (!evaluateParams(target)) return;
		
		Location entityLocation = target.asLocation();
		EntityEquipment equipment = target.asEntity().getEquipment();
		
		if (mainHand.get()) {
			entityLocation.getWorld().dropItemNaturally(entityLocation, equipment.getItemInMainHand());
			equipment.setItemInMainHand(null);
		}
		
		if (offHand.get()) {
			entityLocation.getWorld().dropItemNaturally(entityLocation, equipment.getItemInOffHand());
			equipment.setItemInOffHand(null);
		}
		
		if (helmet.get()) {
			entityLocation.getWorld().dropItemNaturally(entityLocation, equipment.getHelmet());
			equipment.setHelmet(null);
		}
		
		if (chestplate.get()) {
			entityLocation.getWorld().dropItemNaturally(entityLocation, equipment.getChestplate());
			equipment.setChestplate(null);
		}
		
		if (leggings.get()) {
			entityLocation.getWorld().dropItemNaturally(entityLocation, equipment.getLeggings());
			equipment.setLeggings(null);
		}
		
		if (boots.get()) {
			entityLocation.getWorld().dropItemNaturally(entityLocation, equipment.getBoots());
			equipment.setBoots(null);
		}
	}
	
	private boolean evaluateParams(Target target) {
		return mainHand.evaluate(target)
				&& offHand.evaluate(target)
				&& helmet.evaluate(target)
				&& chestplate.evaluate(target)
				&& leggings.evaluate(target)
				&& boots.evaluate(target);
	}
	
}
