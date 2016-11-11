package com.lesserhydra.praedagrandis.abilities;

import com.lesserhydra.praedagrandis.arguments.ArgumentBlock;
import com.lesserhydra.praedagrandis.arguments.GrandLocation;
import com.lesserhydra.praedagrandis.targeters.Target;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

class ForceAbility implements Ability {
	
	private final double forceAmount;
	private final GrandLocation targetLocation;

	ForceAbility(ArgumentBlock args) {
		forceAmount = args.getDouble(true, 0D,		"forceamount", "force", "amount", "a");
		targetLocation = args.getLocation(false, GrandLocation.buildFromString("F+1"),	"targetlocation", "target", "tloc", "t");
	}

	@Override
	public void run(Target target) {
		if (!target.isEntity()) return;
		LivingEntity targetEntity = target.asEntity();
		
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
