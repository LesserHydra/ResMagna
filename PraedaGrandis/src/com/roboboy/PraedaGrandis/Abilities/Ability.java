package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
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
	
	public Ability(ItemSlotType slotType, ActivatorType activator, Targeter targeter) {
		this.slotType = slotType;
		this.activator = activator;
		this.targeter = targeter;
	}
	
	public final void activate(ItemSlotType type, Target target) {
		if (type.isSubtypeOf(slotType)) activate(target);
	}
	
	public final void activate(Target target) {
		for (Target t : targeter.getTargets(target)) {
			if (!t.isNull()) execute(t);
		}
	}
	
	protected abstract void execute(Target target);
	
	public final ActivatorType getActivator() {
		return activator;
	}
	
	public final ItemSlotType getSlotType() {
		return slotType;
	}

	public final void setTimerDelay(long timerDelay) {
		this.timerDelay = timerDelay;
	}
	
	public final long getTimerDelay() {
		return timerDelay;
	}
	
	/*private final boolean slotTypeIsRepresented(EnumSet<ItemSlotType> slotTypes) {
		for (ItemSlotType slot : slotTypes) {
			if (slot.isSubtypeOf(this.slotType)) return true;
		}
		return false;
	}*/
}
