package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Particle;

class ParticleAbility implements Ability {
	
	private final Particle particleType;
	private final Evaluators.ForFloat offsetX;
	private final Evaluators.ForFloat offsetY;
	private final Evaluators.ForFloat offsetZ;
	private final Evaluators.ForFloat speed;
	private final Evaluators.ForInt amount;
	private final Evaluators.ForLocation centerLocation;

	ParticleAbility(ArgumentBlock args) {
		this.particleType = args.getEnum(true, Particle.BARRIER,	"particlename", "particle", "name", "type", "n");
		this.amount = args.getInteger(true, 1,						"amount", "amnt", "a");
		
		this.centerLocation = args.getLocation(false, GrandLocation.CURRENT,	"location", "loc", "l");
		
		
		Evaluators.ForFloat spread = args.getFloat(false, 0.5F,		"spread", "radius", "sprd", "r");
		Evaluators.ForFloat spreadH = args.getFloat(false, spread,	"spreadh", "sprdh", "sh", "rh");
		Evaluators.ForFloat spreadV = args.getFloat(false, spread,	"spreadv", "sprdv", "sv", "rv");
		this.offsetX = args.getFloat(false, spreadH,			"spreadx", "sx", "x");
		this.offsetY = args.getFloat(false, spreadV,			"spready", "sy", "y");
		this.offsetZ = args.getFloat(false, spreadH,			"spreadz", "sz", "z");
		
		
		this.speed = args.getFloat(false, 0F,	"speed", "s");
	}

	@Override
	public void run(Target target) {
		//Verify parameters
		if (!(offsetX.evaluate(target)
				&& offsetY.evaluate(target)
				&& offsetZ.evaluate(target)
				&& speed.evaluate(target)
				&& amount.evaluate(target)
				&& centerLocation.evaluate(target))) return;
		
		centerLocation.get().getWorld().spawnParticle(particleType, centerLocation.get(), amount.get(),
				offsetX.get(), offsetY.get(), offsetZ.get(), speed.get());
	}

}
