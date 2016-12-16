package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.ResMagna;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

class SpinAbility implements Ability {
	
	private final Evaluators.ForInt degrees;
	private final Evaluators.ForInt duration;
	private final Evaluators.ForLong updateDelay;
	
	SpinAbility(ArgumentBlock args) {
		this.degrees = args.getInteger(true, 0,			"degrees", "amount", "deg");
		this.duration = args.getInteger(true, 0, 		"duration", "ticks", "time", "dur");
		this.updateDelay = args.getLong(false, 5,	    "updatedelay", "delay");
	}

	@Override
	public void run(Target target) {
		if (!target.isEntity()) {
			GrandLogger.log("Tried to run teleport ability with invalid target.", LogType.RUNTIME_ERRORS);
			return;
		}
		
		if (!(degrees.evaluate(target)
				&& duration.evaluate(target)
				&& updateDelay.evaluate(target))) return;
		
		int numberOfUpdates = (int) Math.ceil(duration.get()/updateDelay.get());
		float degreesPerUpdate = degrees.get()/numberOfUpdates;
		SpinTimer spinTimer = new SpinTimer(target.asEntity(), numberOfUpdates, degreesPerUpdate);
		spinTimer.runTaskTimer(ResMagna.plugin, 0L, updateDelay.get());
	}

	private static class SpinTimer extends BukkitRunnable {
		private final LivingEntity targetEntity;
		private final int numberOfUpdates;
		private final float degreesPerUpdate;
		
		private int timesRun = 0;
		private float newYaw;
		
		private SpinTimer(LivingEntity targetEntity, int numberOfUpdates, float degreesPerUpdate) {
			this.targetEntity = targetEntity;
			this.numberOfUpdates = numberOfUpdates;
			this.degreesPerUpdate = degreesPerUpdate;
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
