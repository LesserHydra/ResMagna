package com.lesserhydra.praedagrandis.function;

import com.lesserhydra.praedagrandis.targeters.Target;
import com.lesserhydra.praedagrandis.PraedaGrandis;

class DelayLine extends FunctionLine {
	
	private final long delayAmount;
	
	DelayLine(long delayAmount) { this.delayAmount = delayAmount; }
	
	@Override
	public void run(Target target) {
		PraedaGrandis.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
				PraedaGrandis.plugin,
				() -> nextLine.run(target),
				delayAmount);
	}
	
}
