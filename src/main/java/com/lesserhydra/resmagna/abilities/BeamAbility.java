package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.ResMagna;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.function.Functor;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeter;
import com.lesserhydra.resmagna.targeters.Targeters;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.stream.Collectors;

class BeamAbility implements Ability {
	
	private final double speed;			//Distance per step
	private final int numSteps;			//Steps per update
	private final long delay;			//Delay between updates
	private final double maxDistance;	//Maximum traversal distance
	private final long maxTicks;		//Maximum time running
	
	private final boolean ignoreBlocks;
	private final boolean ignoreEntities;
	
	private final double spreadX;
	private final double spreadY;
	private final double spreadZ;
	
	private final Functor onStep;
	private final Functor onHitBlock;
	private final Functor onHitEntity;
	private final Functor onEnd;
	
	private final GrandLocation originLocation;
	private final GrandLocation targetLocation;
	
	private final Targeter homingTargeter;
	private final double homingForce;
	
	private final Targeter ignoreTargeter;
	
	BeamAbility(ArgumentBlock args) {
		speed = args.getDouble(true, 0D,			"speed", "spd", "s");
		numSteps = args.getInteger(false, 1,		"numsteps", "steps", "nstep");
		delay = args.getLong(false, 10L,			"delay", "dly", "d");
		maxDistance = args.getDouble(false, 50D,	"maxdistance", "distance", "maxdis", "mdis");
		maxTicks = args.getLong(false, 200L,		"maxticks", "ticks", "maxt");
		
		ignoreBlocks = args.getBoolean(false, false,	"ignoreblocks", "notblocks", "noblock", "nb", "ib");
		ignoreEntities = args.getBoolean(false, false,	"ignoreentities", "notentities", "noentity", "noent", "ient", "ne", "ie");
		
		double spread = args.getDouble(false, 0.5,		"spread", "sprd", "s");
		double spreadH = args.getDouble(false, spread,	"spreadh", "sprdh", "sh");
		double spreadV = args.getDouble(false, spread,	"spreadv", "sprdv", "sv");
		spreadX = args.getDouble(false, spreadH,		"spreadx", "sx");
		spreadY = args.getDouble(false, spreadV,		"spready", "sy");
		spreadZ = args.getDouble(false, spreadH,		"spreadz", "sz");
		
		originLocation = args.getLocation(false, GrandLocation.buildFromString("Y+1.62"),		"originlocation", "origin", "oloc");
		targetLocation = args.getLocation(false, GrandLocation.buildFromString("Y+1.62 F+1"),	"targetlocation", "target", "tloc");
		
		homingTargeter = args.getTargeter(false, Targeters.NONE,			"homingtarget", "hometarget", "htarget");
		homingForce = args.getDouble(homingTargeter != Targeters.NONE, 0D,	"homingforce", "homeforce", "hforce");
		
		ignoreTargeter = args.getTargeter(false, Targeters.ACTIVATOR,			"ignore");
		
		Functor onHit = args.getFunction(false, Functor.NONE,	"onhit", "hit");
		onHitBlock = args.getFunction(false, onHit,				"onhitblock", "hitblock", "hitb");
		onHitEntity = args.getFunction(false, onHit,			"onhitentity", "hitentity", "hite");
		
		onStep = args.getFunction(false, Functor.NONE,	    "onstep", "step");
		onEnd = args.getFunction(false, Functor.NONE,	    "onend", "end");
	}

	@Override
	public void run(Target target) {
		//Get beam target, if exists
		Target homingTarget = homingTargeter.getRandomTarget(target);
		//Initialize beam
		new BeamTimer(target, homingTarget).runTaskTimer(ResMagna.plugin, 0L, delay);
	}
	
	private class BeamTimer extends BukkitRunnable {
		private final Target homing;
		
		private Location currentLocation;
		private Vector currentVelocity;
		private Target beamTarget;
		private double totalDistance = 0;
		private double totalTicks = 0;
		
		BeamTimer(Target target, Target homingTarget) {
			homing = homingTarget;
			Location startLocation = originLocation.calculate(target);
			currentVelocity = targetLocation.calculateDirection(target, startLocation).normalize().multiply(speed);
			
			currentLocation = startLocation;
			beamTarget = target.set(Target.from(startLocation), target.current());
		}
		
		@Override
		public void run() {
			//Update, one step at a time
			boolean done = stepBeam();
			//Update total number of ticks
			totalTicks += delay;
			//End beam, if appropriate
			if (done || totalTicks >= maxTicks) stopBeam();
		}

		private boolean stepBeam() {
			//Update, one step at a time
			for (int i = 0; i < numSteps; i++) {
				//Change velocity for homing
				calculateHomingVelocity();
				//Move beam
				currentLocation = currentLocation.add(currentVelocity);
				beamTarget = beamTarget.target(Target.from(currentLocation));
				totalDistance += speed;
				//Run onStep
				onStep.run(beamTarget);
				//Check for end, and run appropriate hit abilities
				if (checkForEnd()) return true;
			}
			return false;
		}
		
		private void calculateHomingVelocity() {
			if (homing.isNull()) return;
			//Get direction to target
			Vector homingForceVector = homing.asLocation().toVector().subtract(currentLocation.toVector());
			//Set magnitude
			homingForceVector.normalize().multiply(homingForce);
			//Add to current velocity
			currentVelocity.add(homingForceVector);
			//Set magnitude
			currentVelocity.normalize().multiply(speed);
		}

		private void stopBeam() {
			onEnd.run(beamTarget);
			cancel();
		}
		
		private boolean checkForEnd() {
			return !ignoreEntities && hitEntity()
					|| !ignoreBlocks && hitBlock()
					|| totalDistance >= maxDistance;
		}
		
		private boolean hitBlock() {
			Material blockType = currentLocation.getBlock().getType();
			if (!blockType.isSolid()) return false;
			onHitBlock.run(beamTarget);
			return true;
		}
		
		private boolean hitEntity() {
			Set<LivingEntity> toIgnore = ignoreTargeter.getTargets(beamTarget).stream()
					.filter(Target::isEntity)
					.map(Target::asEntity)
					.collect(Collectors.toSet());
			
			boolean hit = false;
			for (Entity entity : currentLocation.getWorld().getNearbyEntities(currentLocation, spreadX, spreadY, spreadZ)) {
				if (!(entity instanceof LivingEntity)) continue;
				if (toIgnore.contains(entity)) continue;
				onHitEntity.run(beamTarget.target(Target.from((LivingEntity) entity)));
				hit = true;
			}
			return hit;
		}
		
	}

}
