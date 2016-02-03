package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import com.roboboy.PraedaGrandis.MarkerBuilder;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

public class BeamAbility extends Ability
{	
	private final double speed;			//Distance per step
	private final int numSteps;			//Steps per update
	private final long delay;			//Delay between updates
	private final double maxDistance;	//Maximum traversal distance
	
	//private final GrandAbility onStep;
	//private final GrandAbility onEnd;
	
	private final GrandLocation originLocation;
	private final GrandLocation targetLocation;
	
	private class BeamTimer extends BukkitRunnable {
		private final LivingEntity marker;
		//private final Target beamTarget;
		private final Vector velocity;
		
		private double totalDistance = 0;
		
		BeamTimer(Target target) {
			Location startLocation = originLocation.calculate(target);
			velocity = targetLocation.calculateDirection(target, startLocation).normalize().multiply(speed);
			
			marker = MarkerBuilder.buildMarker(startLocation);
			//beamTarget = target.target(marker);
			
			runTaskTimer(PraedaGrandis.plugin, 0L, delay);
		}
		
		@Override
		public void run() {
			//Update, one step at a time
			for (int i = 0; i < numSteps; i++) {
				if (totalDistance > maxDistance) break;
				totalDistance += speed;
				marker.teleport(marker.getLocation().add(velocity));
				
				if (totalDistance <= maxDistance) {
					stopBeam();
					return;
				}
				
				//onStep.run(beamTarget);
			}
		}

		private void stopBeam() {
			//onEnd.run(beamTarget);
			cancel();
		}
		
	}
	
	public BeamAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		speed = args.getDouble("speed", 0D, true);
		numSteps = args.getInteger("numsteps", 1, false);
		delay = args.getLong("delay", 10L, false);
		maxDistance = args.getDouble("maxdistance", 100D, false);
		//TODO: limit time as well
		
		originLocation = args.getLocation("originlocation", new GrandLocation(), false);
		targetLocation = args.getLocation("targetlocation", new GrandLocation(), false);
	}

	@Override
	protected void execute(Target target) {
		new BeamTimer(target);
	}

}
