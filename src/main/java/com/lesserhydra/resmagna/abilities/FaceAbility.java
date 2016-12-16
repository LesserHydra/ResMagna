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

class FaceAbility implements Ability {
	
	private final Evaluators.ForLocation faceLocation;
	
	FaceAbility(ArgumentBlock args) {
		this.faceLocation = args.getLocation(true, GrandLocation.CURRENT,  "other", "target", null);
	}
	
	@Override
	public void run(Target target) {
		//Validate target
		if (!target.isEntity()) {
			GrandLogger.log("Tried to run face ability with invalid target.", LogType.RUNTIME_ERRORS);
			return;
		}
		LivingEntity targetEntity = target.asEntity();
		
		//Validate faceLocation
		if (!faceLocation.evaluate(target)) return;
		
		//Set look direction
		Location eye = targetEntity.getEyeLocation();
		Location face = faceLocation.get();
		Vector direction = new Vector(face.getX() - eye.getX(), face.getY() - eye.getY(), face.getZ() - eye.getZ());
		targetEntity.teleport(targetEntity.getLocation().setDirection(direction));
	}
	
}
