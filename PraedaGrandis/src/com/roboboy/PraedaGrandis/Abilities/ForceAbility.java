package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

class ForceAbility extends Ability
{
	private final double forceAmount;
	private final GrandLocation targetLocation;

	public ForceAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		forceAmount = args.getDouble("amount", 0D, true);
		targetLocation = args.getLocation("targetlocation", new GrandLocation("F+1"), false);
	}

	@Override
	protected void execute(Target target) {
		Location calculatedTargetLocation = targetLocation.calculate(target);
		if (calculatedTargetLocation == null) return;
		
		//Old velocity plus new force vector
		LivingEntity targetEntity = target.get();
		targetEntity.setVelocity(targetEntity.getVelocity().add(getForceVector(targetEntity.getLocation(), calculatedTargetLocation)));
	}
	
	private Vector getForceVector(Location from, Location towards) {
		//Force vector pointing to targetLocation...
		Vector result = towards.toVector().subtract(from.toVector());
		//...with length of forceAmount
		return result.normalize().multiply(forceAmount);
	}

}
