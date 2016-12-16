package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.ResMagna;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.function.Functor;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeters;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

class BeamAbility implements Ability {
	
	private final Evaluators.ForDouble speed;			//Distance per step
	private final Evaluators.ForInt numSteps;			//Steps per update
	private final Evaluators.ForLong delay;			//Delay between updates
	private final Evaluators.ForDouble maxDistance;	//Maximum traversal distance
	private final Evaluators.ForLong maxTicks;		//Maximum time running
	
	private final Evaluators.ForBoolean ignoreBlocks;
	private final Evaluators.ForBoolean ignoreEntities;
	
	private final Evaluators.ForDouble spreadX;
	private final Evaluators.ForDouble spreadY;
	private final Evaluators.ForDouble spreadZ;
	
	private final Functor onStep;
	private final Functor onHitBlock;
	private final Functor onHitEntity;
	private final Functor onEnd;
	
	private final Evaluators.ForLocation originLocation;
	private final Evaluators.ForLocation targetLocation;
	
	private final Evaluators.ForLocation homingTargeter;
	private final Evaluators.ForDouble homingForce;
	
	private final Evaluators.ForEntity ignoreTargeter;
	
	BeamAbility(ArgumentBlock args) {
		this.speed = args.getDouble(true, 0D,			"speed", "spd", "s");
		this.numSteps = args.getInteger(false, 1,		"numsteps", "steps", "nstep");
		this.delay = args.getLong(false, 10L,			"delay", "dly", "d");
		this.maxDistance = args.getDouble(false, 50D,	"maxdistance", "distance", "maxdis", "mdis");
		this.maxTicks = args.getLong(false, 200L,		"maxticks", "ticks", "maxt");
		
		this.ignoreBlocks = args.getBoolean(false, false,	"ignoreblocks", "notblocks", "noblock", "nb", "ib");
		this.ignoreEntities = args.getBoolean(false, false,	"ignoreentities", "notentities", "noentity", "noent", "ient", "ne", "ie");
		
		Evaluators.ForDouble spread = args.getDouble(false, 0.5,		"spread", "sprd", "s");
		Evaluators.ForDouble spreadH = args.getDouble(false, spread,	"spreadh", "sprdh", "sh");
		Evaluators.ForDouble spreadV = args.getDouble(false, spread,	"spreadv", "sprdv", "sv");
		this.spreadX = args.getDouble(false, spreadH,		"spreadx", "sx");
		this.spreadY = args.getDouble(false, spreadV,		"spready", "sy");
		this.spreadZ = args.getDouble(false, spreadH,		"spreadz", "sz");
		
		this.originLocation = args.getLocation(false, GrandLocation.buildFromString("Y+1.62"),		"originlocation", "origin", "oloc");
		this.targetLocation = args.getLocation(false, GrandLocation.buildFromString("Y+1.62 F+1"),	"targetlocation", "target", "tloc");
		
		this.homingTargeter = args.getLocation(false, GrandLocation.CURRENT,			"homingtarget", "hometarget", "htarget");
		this.homingForce = args.getDouble(false, 0D,	"homingforce", "homeforce", "hforce");
		
		this.ignoreTargeter = args.getEntity(false, Targeters.ACTIVATOR,			"ignore");
		
		Functor onHit = args.getFunction(false, Functor.NONE,	"onhit", "hit");
		this.onHitBlock = args.getFunction(false, onHit,				"onhitblock", "hitblock", "hitb");
		this.onHitEntity = args.getFunction(false, onHit,			"onhitentity", "hitentity", "hite");
		
		this.onStep = args.getFunction(false, Functor.NONE,	    "onstep", "step");
		this.onEnd = args.getFunction(false, Functor.NONE,	    "onend", "end");
	}

	@Override
	public void run(Target target) {
		if (!evaluateParams(target)) return;
		
		Location startLocation = originLocation.get();
		Vector startVelocity = targetLocation.get().toVector().subtract(startLocation.toVector())
				.normalize().multiply(speed.get());
		
		//Initialize beam
		BeamTimer timer = new BeamTimer(target, startLocation, startVelocity, speed.get(), numSteps.get(), delay.get(),
				maxDistance.get(), maxTicks.get(), ignoreBlocks.get(), ignoreEntities.get(), spreadX.get(), spreadY.get(),
				spreadZ.get(), onStep, onHitBlock, onHitEntity, onEnd, ignoreTargeter, homingTargeter, homingForce.get());
		timer.runTaskTimer(ResMagna.plugin, 0L, delay.get());
	}
	
	private boolean evaluateParams(Target target) {
		return speed.evaluate(target)
				&& numSteps.evaluate(target)
				&& delay.evaluate(target)
				&& maxDistance.evaluate(target)
				&& maxTicks.evaluate(target)
				&& ignoreBlocks.evaluate(target)
				&& ignoreEntities.evaluate(target)
				&& spreadX.evaluate(target)
				&& spreadY.evaluate(target)
				&& spreadZ.evaluate(target)
				&& originLocation.evaluate(target)
				&& targetLocation.evaluate(target)
				&& homingForce.evaluate(target);
	}
	
	private static class BeamTimer extends BukkitRunnable {
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
		
		private final Evaluators.ForEntity ignoreTargeter;
		
		private final Evaluators.ForLocation homing;
		private final double homingForce;
		
		//private final Targeter ignoreTargeter;
		
		
		private Location currentLocation;
		private Vector currentVelocity;
		private Target beamTarget;
		private double totalDistance = 0;
		private double totalTicks = 0;
		
		BeamTimer(Target target, Location startLocation, Vector startVelocity,
		          double speed, int numSteps, long delay, double maxDistance, long maxTicks,
		          boolean ignoreBlocks, boolean ignoreEntities, double spreadX, double spreadY, double spreadZ,
		          Functor onStep, Functor onHitBlock, Functor onHitEntity, Functor onEnd, Evaluators.ForEntity ignoreTargeter,
		          Evaluators.ForLocation homing, double homingForce) {
			this.speed = speed;
			this.numSteps = numSteps;
			this.delay = delay;
			this.maxDistance = maxDistance;
			this.maxTicks = maxTicks;
			this.ignoreBlocks = ignoreBlocks;
			this.ignoreEntities = ignoreEntities;
			this.spreadX = spreadX;
			this.spreadY = spreadY;
			this.spreadZ = spreadZ;
			this.onStep = onStep;
			this.onHitBlock = onHitBlock;
			this.onHitEntity = onHitEntity;
			this.onEnd = onEnd;
			
			this.ignoreTargeter = ignoreTargeter;
			
			this.homing = homing;
			this.homingForce = homingForce;
			
			this.currentVelocity = startVelocity;
			this.currentLocation = startLocation;
			this.beamTarget = target.set(Target.from(startLocation), target.current());
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
			if (homing.evaluate(beamTarget, false)) return;
			//Get direction to target
			Vector homingForceVector = homing.get().toVector().subtract(currentLocation.toVector());
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
			Set<Entity> toIgnore = new HashSet<>();
			if (ignoreTargeter.evaluate(beamTarget)) toIgnore.add(ignoreTargeter.get());
			
			/*ignoreTargeter.getTargets(beamTarget).stream()
					.filter(Target::isEntity)
					.map(Target::asEntity)
					.collect(Collectors.toSet());*/
			
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
