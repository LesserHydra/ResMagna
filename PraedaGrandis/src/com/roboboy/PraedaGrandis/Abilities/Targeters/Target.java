package com.roboboy.PraedaGrandis.Abilities.Targeters;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Target
{
	private final LivingEntity currentTarget;
	private final Player holder;
	private final LivingEntity activatorTarget;
	//private List<LivingEntity> oldTarget;
	
	public Target(LivingEntity currentTarget, Player holder, LivingEntity activatorTarget)
	{
		this.currentTarget = currentTarget;
		this.holder = holder;
		this.activatorTarget = activatorTarget;
	}
	
	public LivingEntity get() {
		return currentTarget;
	}
	
	public Player getHolder() {
		return holder;
	}
	
	public LivingEntity getActivator() {
		return activatorTarget;
	}
	
	public Target target(LivingEntity newTarget) {
		return new Target(newTarget, holder, activatorTarget);
	}
	
	public Target targetHolder() {
		return new Target(holder, holder, activatorTarget);
	}
	
	public Target targetActivator() {
		return new Target(activatorTarget, holder, activatorTarget);
	}
}
