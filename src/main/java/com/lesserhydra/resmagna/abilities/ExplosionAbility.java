package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeter;
import com.lesserhydra.resmagna.targeters.Targeters;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

class ExplosionAbility implements Ability {
	
	private final Evaluators.ForFloat power;
	private final Evaluators.ForBoolean setFire;
	private final Evaluators.ForBoolean breakBlocks;
	private final Evaluators.ForLocation location;
	private final Evaluators.ForEntity damager;
	
	ExplosionAbility(ArgumentBlock args) {
		this.power = args.getFloat(false, 0F,					"power", "yield", "p");
		this.setFire = args.getBoolean(false, false,				"setfire", "fire");
		this.breakBlocks = args.getBoolean(false, false,			"breakblocks", "blocks", "break");
		this.location = args.getLocation(false, GrandLocation.CURRENT,     "location", "loc", "l");
		this.damager = args.getEntity(false, Targeters.NONE,   "damager", "source", "dmgr");
	}

	@Override
	public void run(Target target) {
		if (!(power.evaluate(target)
				&& setFire.evaluate(target)
				&& breakBlocks.evaluate(target)
				&& location.evaluate(target))) return;
		
		Location loc = location.get();
		
		//Mark entities in radius as damaged by damagerEntity, if exists
		if (damager.evaluate(target, false)) {
			float damageRadius = power.get() * 2;
			double damageRadiusSquared = damageRadius * damageRadius;
			for (Entity entity : loc.getWorld().getNearbyEntities(loc, damageRadius, damageRadius, damageRadius)) {
				if (!(entity instanceof LivingEntity)) continue;
				if (loc.distanceSquared(entity.getLocation()) > damageRadiusSquared) continue;
				((LivingEntity)entity).damage(0D, damager.get());
			}
		}
		
		//Create explosion
		loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power.get(), setFire.get(), breakBlocks.get());
	}

}
