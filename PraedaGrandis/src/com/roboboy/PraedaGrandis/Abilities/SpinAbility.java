package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class SpinAbility extends Ability
{
	private final int degrees;
	private final int duration;
	private final long updateDelay;
	private final boolean reverse;
	
	public SpinAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		degrees = args.getInteger(true, 0,			"degrees", "amount", "deg", "a");
		duration = args.getInteger(true, 0, 		"duration", "ticks", "time", "dur", "d", "t");
		updateDelay = args.getLong(false, 5,		"updateDelay", "delay", "ud");
		reverse = args.getBoolean(false, false,		"reverse", "r");
	}

	@Override
	protected void execute(Target target) {
		LivingEntity targetEntity = target.getEntity();
		if (targetEntity == null) return;
		
		new SpinTimer(targetEntity).runTaskTimer(PraedaGrandis.plugin, 0L, updateDelay);
	}

	private class SpinTimer extends BukkitRunnable
	{
		private final LivingEntity targetEntity;
		private final int numberOfUpdates;
		private final float degreesPerUpdate;
		
		private int timesRun = 0;
		private float newYaw;
		
		private SpinTimer(LivingEntity targetEntity) {
			this.targetEntity = targetEntity;
			this.numberOfUpdates = (int) Math.ceil(duration/updateDelay);
			this.degreesPerUpdate = degrees/numberOfUpdates * (reverse ? -1 : 1);
			
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
