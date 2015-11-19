package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;


/**
 * Base Ability
 */
public abstract class Ability
{
	private final ItemSlotType slotType;
	private final ActivatorType activator;
	private final Targeter targeter;
	private long timerDelay;
	
	public Ability(ItemSlotType slotType, ActivatorType activator, Targeter targeter)
	{
		this.slotType = slotType;
		this.activator = activator;
		this.targeter = targeter;
	}
	
	public final void activate(ItemSlotType slot, Target target)
	{
		PraedaGrandis.plugin.getLogger().info("Item in " + slot.name() + " slot, asking for " + slotType.name());
		if (slot.isSubtypeOf(slotType)) {
			for (Target t : targeter.getTargets(target)) {
				execute(t);
			}
		}
	}
	
	protected abstract void execute(Target target);
	
	public ActivatorType getActivator() {
		return activator;
	}

	public final void setTimerDelay(long timerDelay) {
		this.timerDelay = timerDelay;
	}
	
	public final long getTimerDelay() {
		return timerDelay;
	}
}
