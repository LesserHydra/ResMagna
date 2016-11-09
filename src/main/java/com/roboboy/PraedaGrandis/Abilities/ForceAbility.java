package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

class ForceAbility implements Ability {
	
	private final double forceAmount;
	private final GrandLocation targetLocation;

	ForceAbility(BlockArguments args) {
		forceAmount = args.getDouble(true, 0D,		"forceamount", "force", "amount", "a");
		targetLocation = args.getLocation(false, GrandLocation.buildFromString("F+1"),	"targetlocation", "target", "tloc", "t");
	}

	@Override
	public void execute(Target target) {
		LivingEntity targetEntity = target.getEntity();
		if (targetEntity == null) return;
		
		Location calculatedTargetLocation = targetLocation.calculate(target);
		if (calculatedTargetLocation == null) return;
		
		//Old velocity plus new force vector
		targetEntity.setVelocity(targetEntity.getVelocity().add(getForceVector(targetEntity.getLocation(), calculatedTargetLocation)));
	}
	
	private Vector getForceVector(Location from, Location towards) {
		//Force vector pointing to targetLocation...
		Vector result = towards.toVector().subtract(from.toVector());
		//...with length of forceAmount
		return result.normalize().multiply(forceAmount);
	}

}