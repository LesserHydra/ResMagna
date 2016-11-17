package com.lesserhydra.resmagna.function;

import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.ResMagna;

class DelayLine extends FunctionLine {
	
	private final long delayAmount;
	
	DelayLine(long delayAmount) { this.delayAmount = delayAmount; }
	
	@Override
	public void run(Target target) {
		ResMagna.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
				ResMagna.plugin,
				() -> nextLine.run(target),
				delayAmount);
	}
	
}
