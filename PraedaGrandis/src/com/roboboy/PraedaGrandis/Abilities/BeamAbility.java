package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.CurrentTargeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.FunctionRunner;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

public class BeamAbility extends Ability
{	
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
	
	public BeamAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		
		speed = args.getDouble("speed", 0D, true);
		numSteps = args.getInteger("numsteps", 1, false);
		delay = args.getLong("delay", 10L, false);
		maxDistance = args.getDouble("maxdistance", 50D, false);
		maxTicks = args.getLong("maxticks", 200L, false);
		
		ignoreBlocks = args.getBoolean("ignoreblocks", false, false);
		ignoreEntities = args.getBoolean("ignoreentities", false, false);
		
		double spread = args.getDouble("spread", 0.5, false);
		spreadX = args.getDouble("spreadx", spread, false);
		spreadY = args.getDouble("spready", spread, false);
		spreadZ = args.getDouble("spreadz", spread, false);
		
		originLocation = args.getLocation("originlocation", new GrandLocation(), false);
		targetLocation = args.getLocation("targetlocation", new GrandLocation(), false);
		
		homingTargeter = args.getTargeter("homingtarget", new CurrentTargeter(), false);
		homingForce = args.getDouble("homingforce", 0D, false);
		
		String onHitString = args.get("onhit", null, false);
		onHitBlock = new FunctionRunner(args.get("onhitblock", onHitString, false));
		onHitEntity = new FunctionRunner(args.get("onhitentity", onHitString, false));
		
		onStep = new FunctionRunner(args.get("onstep", null, false));
		onEnd = new FunctionRunner(args.get("onend", null, false));
	}

	@Override
	protected void execute(Target target) {
		//Get beam target, if exists
		Target homingTarget = null;
		if (homingTargeter != null) homingTarget = homingTargeter.getRandomTarget(target);
		//Initialize beam
		new BeamTimer(target, homingTarget).runTaskTimer(PraedaGrandis.plugin, 0L, delay);
	}
	
	private class BeamTimer extends BukkitRunnable {
		private final LivingEntity shooter;
		private final LivingEntity homing;
		
		private Location currentLocation;
		private Vector currentVelocity;
		private Target beamTarget;
		private double totalDistance = 0;
		private double totalTicks = 0;
		
		BeamTimer(Target target, Target homingTarget) {
			shooter = target.getEntity();
			homing = (homingTarget != null ? homingTarget.getEntity() : null);
			Location startLocation = originLocation.calculate(target);
			currentVelocity = targetLocation.calculateDirection(target, startLocation).normalize().multiply(speed);
			
			currentLocation = startLocation;
			beamTarget = target;
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
				beamTarget = beamTarget.target(currentLocation);
				totalDistance += speed;
				//Run onStep
				onStep.run(beamTarget);
				//Check for end, and run appropriate hit abilities
				if (checkForEnd()) return true;
			}
			return false;
		}
		
		private void calculateHomingVelocity() {
			if (homing == null) return;
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
			if (!ignoreEntities && hitEntity()) return true;
			if (!ignoreBlocks && hitBlock()) return true;
			if (totalDistance >= maxDistance) return true;
			return false;
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
				onHitEntity.run(beamTarget.target((LivingEntity) entity));
				hit = true;
			}
			return hit;
		}
		
	}

}
