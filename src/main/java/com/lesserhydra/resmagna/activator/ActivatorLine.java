package com.lesserhydra.resmagna.activator;

import com.lesserhydra.resmagna.arguments.ItemSlotType;
import com.lesserhydra.resmagna.function.Functor;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeter;

public class ActivatorLine {

	private final Functor ability;
	private final Targeter targeter;
	private final ItemSlotType slotType;
	private final ActivatorType activator;
	private long timerDelay;

	ActivatorLine(Functor ability, Targeter targeter, ItemSlotType slotType, ActivatorType activatorType) {
		this.ability = ability;
		this.targeter = targeter;
		this.slotType = slotType;
		this.activator = activatorType;
	}

	public final void activate(ItemSlotType type, Target target) {
		if (!type.isSubtypeOf(slotType)) return;
		targeter.getTargets(target).forEach(ability::run);
	}
	
	public final ActivatorType getType() { return activator; }
	
	public final ItemSlotType getRequestedSlot() { return slotType; }
	
	final void setTimerDelay(long timerDelay) { this.timerDelay = timerDelay; }
	
	public final long getTimerDelay() { return timerDelay; }
	
}
