package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.NoneTargeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

class ExplosionAbility extends Ability
{
	private final float power;
	private final boolean setFire;
	private final boolean breakBlocks;
	private final GrandLocation location;
	private final Targeter damagerTargeter;
	
	public ExplosionAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		
		power = args.getFloat(false, 0F,								"power", "yield", "p");
		setFire = args.getBoolean(false, false,							"setfire", "fire");
		breakBlocks = args.getBoolean(false, false,						"breakblocks", "blocks", "break");
		location = args.getLocation(false, new GrandLocation(), 		"location", "loc", "l");
		damagerTargeter = args.getTargeter(false, new NoneTargeter(),	"damager", "source", "dmgr");
	}

	@Override
	protected void execute(Target target) {
		//Get damagerTarget
		Target damagerTarget = damagerTargeter.getRandomTarget(target);
		
		Location calculatedLocation = location.calculate(target);
		if (calculatedLocation == null) return;
		
		LivingEntity damagerEntity = damagerTarget.getEntity();
		
		//Mark entities in radius as damaged by damagerEntity, if exists
		if (damagerEntity != null) {
			float damageRadius = power * 2;
			double damageRadiusSquared = damageRadius * damageRadius;
			for (Entity entity : calculatedLocation.getWorld().getNearbyEntities(calculatedLocation, damageRadius, damageRadius, damageRadius)) {
				if (!(entity instanceof LivingEntity)) continue;
				if (calculatedLocation.distanceSquared(entity.getLocation()) > damageRadiusSquared) continue;
				((LivingEntity)entity).damage(0D, damagerEntity);
			}
		}
		
		//Create explosion
		calculatedLocation.getWorld().createExplosion(calculatedLocation.getX(), calculatedLocation.getY(), calculatedLocation.getZ(), power, setFire, breakBlocks);
	}

}
