package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Function.Functor;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import com.roboboy.PraedaGrandis.Arguments.GrandLocation;
import org.bukkit.Location;
import org.bukkit.Particle;

class ParticleAbility implements Functor {
	
	private final Particle particleType;
	private final float offsetX;
	private final float offsetY;
	private final float offsetZ;
	private final float speed;
	private final int amount;
	private final GrandLocation centerLocation;

	ParticleAbility(ArgumentBlock args) {
		particleType = args.getEnum(true, Particle.BARRIER,	"particlename", "particle", "name", "type", "n");
		amount = args.getInteger(true, 1,						"amount", "amnt", "a");
		
		centerLocation = args.getLocation(false, new GrandLocation(),	"location", "loc", "l");
		
		
		float spread = args.getFloat(false, 0.5F,		"spread", "radius", "sprd", "r");
		float spreadH = args.getFloat(false, spread,	"spreadh", "sprdh", "sh", "rh");
		float spreadV = args.getFloat(false, spread,	"spreadv", "sprdv", "sv", "rv");
		offsetX = args.getFloat(false, spreadH,			"spreadx", "sx", "x");
		offsetY = args.getFloat(false, spreadV,			"spready", "sy", "y");
		offsetZ = args.getFloat(false, spreadH,			"spreadz", "sz", "z");
		
		
		speed = args.getFloat(false, 0F,	"speed", "s");
	}

	@Override
	public void run(Target target) {
		Location calculatedLocation = centerLocation.calculate(target);
		if (calculatedLocation == null) return;
		
		calculatedLocation.getWorld().spawnParticle(particleType, calculatedLocation, amount, offsetX, offsetY, offsetZ, speed);
	}

}
