package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.MarkerBuilder;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

public class ExplosionAbility extends Ability
{
	private final float power;
	private final boolean setFire;
	private final boolean breakBlocks;
	private final GrandLocation location;
	
	public ExplosionAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		
		power = args.getFloat("yield", 0F, true);
		setFire = args.getBoolean("fire", false, false);
		breakBlocks = args.getBoolean("breakBlocks", true, false);
		location = args.getLocation("location", new GrandLocation(0, 0, 0, true, true, true), false);
	}

	@Override
	protected void execute(Target target) {
		Location loc = location.calculate(target.get().getLocation());
		LivingEntity marker = MarkerBuilder.buildMarker(loc);
		float damageRadius = power * 2;
		double damageRadiusSquared = damageRadius * damageRadius;
		//Mark entities in radius as damaged by holder
		for (Entity entity : marker.getNearbyEntities(damageRadius, damageRadius, damageRadius)) {
			if (!(entity instanceof LivingEntity)) continue;
			if (loc.distanceSquared(entity.getLocation()) > damageRadiusSquared) continue;
			((LivingEntity)entity).damage(0D, target.getHolder());
		}
		loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, breakBlocks);
	}

}
