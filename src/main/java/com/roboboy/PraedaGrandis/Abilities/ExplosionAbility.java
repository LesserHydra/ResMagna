package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import com.roboboy.PraedaGrandis.Function.Functor;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Targeters.Targeters;
import com.roboboy.PraedaGrandis.Arguments.GrandLocation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

class ExplosionAbility implements Functor {
	
	private final float power;
	private final boolean setFire;
	private final boolean breakBlocks;
	private final GrandLocation location;
	private final Targeter damagerTargeter;
	
	ExplosionAbility(ArgumentBlock args) {
		power = args.getFloat(false, 0F,							"power", "yield", "p");
		setFire = args.getBoolean(false, false,						"setfire", "fire");
		breakBlocks = args.getBoolean(false, false,					"breakblocks", "blocks", "break");
		location = args.getLocation(false, new GrandLocation(),     "location", "loc", "l");
		damagerTargeter = args.getTargeter(false, Targeters.NONE,   "damager", "source", "dmgr");
	}

	@Override
	public void run(Target target) {
		//Get damagerTarget
		Target damagerTarget = damagerTargeter.getRandomTarget(target);
		
		Location calculatedLocation = location.calculate(target);
		if (calculatedLocation == null) return;
		
		LivingEntity damagerEntity = damagerTarget.asEntity();
		
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
