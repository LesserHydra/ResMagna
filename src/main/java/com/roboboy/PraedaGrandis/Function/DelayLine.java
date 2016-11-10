package com.roboboy.PraedaGrandis.Function;

import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.PraedaGrandis;

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
