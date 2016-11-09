package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Abilities.Targeters.NoneTargeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargetEntity;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargetLocation;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.FunctionRunner;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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
	
	private final FunctionRunner onStep;
	private final FunctionRunner onHitBlock;
	private final FunctionRunner onHitEntity;
	private final FunctionRunner onEnd;
	
	private final GrandLocation originLocation;
	private final GrandLocation targetLocation;
	
	private final Targeter homingTargeter;
	private final double homingForce;
	
	BeamAbility(BlockArguments args) {
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
		
		homingTargeter = args.getTargeter(false, new NoneTargeter(),					"homingtarget", "hometarget", "htarget");
		homingForce = args.getDouble(!(homingTargeter instanceof NoneTargeter), 0D,		"homingforce", "homeforce", "hforce");
		
		FunctionRunner onHit = args.getFunction(false, null,	"onhit", "hit");
		onHitBlock = args.getFunction(false, onHit,				"onhitblock", "hitblock", "hitb");
		onHitEntity = args.getFunction(false, onHit,			"onhitentity", "hitentity", "hite");
		
		onStep = args.getFunction(false, null,		"onstep", "step");
		onEnd = args.getFunction(false, null,		"onend", "end");
	}

	@Override
	public void execute(Target target) {
		//Get beam target, if exists
		Target homingTarget = homingTargeter.getRandomTarget(target);
		//Initialize beam
		new BeamTimer(target, homingTarget).runTaskTimer(PraedaGrandis.plugin, 0L, delay);
	}
	
	private class BeamTimer extends BukkitRunnable {
		private final LivingEntity shooter;
		private final Target homing;
		
		private Location currentLocation;
		private Vector currentVelocity;
		private Target beamTarget;
		private double totalDistance = 0;
		private double totalTicks = 0;
		
		BeamTimer(Target target, Target homingTarget) {
			shooter = target.getEntity();
			homing = homingTarget;
			Location startLocation = originLocation.calculate(target);
			currentVelocity = targetLocation.calculateDirection(target, startLocation).normalize().multiply(speed);
			
			currentLocation = startLocation;
			beamTarget = new Target(new TargetLocation(startLocation), target.getHolder(), target.getCurrent());
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
				beamTarget = beamTarget.target(new TargetLocation(currentLocation));
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
			Vector homingForceVector = homing.getLocation().toVector().subtract(currentLocation.toVector());
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
		
		public boolean checkForEnd() {
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
			boolean hit = false;
			for (Entity entity : currentLocation.getWorld().getNearbyEntities(currentLocation, spreadX, spreadY, spreadZ)) {
				if (entity.equals(shooter)) continue;
				if (!(entity instanceof LivingEntity)) continue;
				onHitEntity.run(beamTarget.target(new TargetEntity((LivingEntity) entity)));
				hit = true;
			}
			return hit;
		}
		
	}

}
