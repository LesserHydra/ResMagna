package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

class ForceAbility implements Ability {
	
	private final Evaluators.ForDouble forceAmount;
	private final Evaluators.ForLocation targetLocation;

	ForceAbility(ArgumentBlock args) {
		this.forceAmount = args.getDouble(true, 0D,		"forceamount", "force", "amount", "a");
		this.targetLocation = args.getLocation(false, GrandLocation.buildFromString("F+1"),	"targetlocation", "target", "tloc", "t");
	}

	@Override
	public void run(Target target) {
		//Verify target
		if (!target.isEntity()) {
			GrandLogger.log("Tried to run force ability with invalid target.", LogType.RUNTIME_ERRORS);
			return;
		}
		LivingEntity targetEntity = target.asEntity();
		
		//Verify parameters
		if (!(forceAmount.evaluate(target)
				&& targetLocation.evaluate(target))) return;
		
		//Old velocity plus new force vector
		targetEntity.setVelocity(targetEntity.getVelocity().add(getForceVector(targetEntity.getLocation(), targetLocation.get())));
	}
	
	private Vector getForceVector(Location from, Location towards) {
		//Force vector pointing to targetLocation...
		Vector result = towards.toVector().subtract(from.toVector());
		//...with length of forceAmount
		return result.normalize().multiply(forceAmount.get());
	}

}
