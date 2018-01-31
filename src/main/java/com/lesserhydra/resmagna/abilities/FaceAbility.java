package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeter;
import com.lesserhydra.resmagna.targeters.Targeters;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

class FaceAbility implements Ability {
	
	private final Targeter otherTargeter;
	
	FaceAbility(ArgumentBlock args) {
		otherTargeter = args.getTargeter(true, Targeters.NONE,  "other", "target", null);
	}
	
	@Override
	public void run(Target target) {
		if (!target.isEntity()) return;
		LivingEntity targetEntity = target.asEntity();
		
		Target otherTarget = otherTargeter.getRandomTarget(target);
		if (!otherTarget.isLocation()) return;
		
		Location eye = targetEntity.getEyeLocation();
		Location face = otherTarget.asLocation();
		Vector direction = new Vector(face.getX() - eye.getX(), face.getY() - eye.getY(), face.getZ() - eye.getZ());
		//direction = direction.normalize();
		
		targetEntity.teleport(targetEntity.getLocation().setDirection(direction));
	}
	
}
