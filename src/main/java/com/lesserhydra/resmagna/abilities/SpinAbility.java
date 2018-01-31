package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.ResMagna;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

class SpinAbility implements Ability.ForEntity {
	
	private final int numberOfUpdates;
	private final float degreesPerUpdate;
	private final long updateDelay;
	
	SpinAbility(ArgumentBlock args) {
		int degrees = args.getInteger(true, 0,			"degrees", "amount", "deg");
		int duration = args.getInteger(true, 0, 		"duration", "ticks", "time", "dur");
		this.updateDelay = args.getLong(false, 5,		"updatedelay", "delay");
		
		this.numberOfUpdates = (int) Math.ceil(duration/updateDelay);
		this.degreesPerUpdate = degrees/numberOfUpdates;
	}

	@Override
	public void run(LivingEntity target) {
		SpinTimer spinTimer = new SpinTimer(target);
		spinTimer.runTaskTimer(ResMagna.plugin, 0L, updateDelay);
	}

	private class SpinTimer extends BukkitRunnable {
		private final LivingEntity targetEntity;
		
		private int timesRun = 0;
		private float newYaw;
		
		private SpinTimer(LivingEntity targetEntity) {
			this.targetEntity = targetEntity;
			this.newYaw = targetEntity.getLocation().getYaw();
		}
		
		@Override public void run() {
			Location newLocation = targetEntity.getLocation().clone();
			newYaw += degreesPerUpdate;
			newLocation.setYaw(newYaw);
			targetEntity.teleport(newLocation);
			timesRun++;
			if (timesRun == numberOfUpdates) this.cancel();
		}
	}
	
}
