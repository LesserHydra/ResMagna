package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

class SpinAbility implements Ability {
	
	private final int numberOfUpdates;
	private final float degreesPerUpdate;
	private final long updateDelay;
	
	SpinAbility(BlockArguments args) {
		int degrees = args.getInteger(true, 0,			"degrees", "amount", "deg");
		int duration = args.getInteger(true, 0, 		"duration", "ticks", "time", "dur");
		this.updateDelay = args.getLong(false, 5,		"updatedelay", "delay");
		
		this.numberOfUpdates = (int) Math.ceil(duration/updateDelay);
		this.degreesPerUpdate = degrees/numberOfUpdates;
	}

	@Override
	public void execute(Target target) {
		LivingEntity targetEntity = target.getEntity();
		if (targetEntity == null) return;
		
		//TODO: Ick.
		new SpinTimer(targetEntity).runTaskTimer(PraedaGrandis.plugin, 0L, updateDelay);
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
