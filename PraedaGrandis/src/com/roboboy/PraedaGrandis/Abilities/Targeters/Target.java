package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Target
{
	private final LivingEntity currentTarget;
	private final Player holder;
	private final LivingEntity activatorTarget;
	private final Map<String, LivingEntity> savedTargets;
	
	/**
	 * Construct a fresh Target with a new save map
	 * @param currentTarget
	 * @param holder
	 * @param activatorTarget
	 */
	public Target(LivingEntity currentTarget, Player holder, LivingEntity activatorTarget)
	{
		this.currentTarget = currentTarget;
		this.holder = holder;
		this.activatorTarget = activatorTarget;
		
		this.savedTargets = new HashMap<>();
	}
	
	/**
	 * Construct a new Target sharing a saved target map
	 * @param currentTarget
	 * @param holder
	 * @param activatorTarget
	 * @param savedTargets Custom save map to share
	 */
	private Target(LivingEntity currentTarget, Player holder, LivingEntity activatorTarget, Map<String, LivingEntity> savedTargets)
	{
		this.currentTarget = currentTarget;
		this.holder = holder;
		this.activatorTarget = activatorTarget;
		this.savedTargets = savedTargets;
	}
	
	/**
	 * Save an entity to all Targets sharing a saved target map
	 * @param saveName Save name
	 * @param toSave LivingEntity to save
	 */
	public void save(String saveName, LivingEntity toSave) {
		savedTargets.put(saveName, toSave);
	}
	
	/**
	 * Get the currently targeted entity
	 * @return Current target entity
	 */
	public LivingEntity get() {
		return currentTarget;
	}
	
	/**
	 * Get the holder player
	 * @return Holder player
	 */
	public Player getHolder() {
		return holder;
	}
	
	/**
	 * Get the entity targeted by whatever originally constructed the Target
	 * @return Activator entity
	 */
	public LivingEntity getActivator() {
		return activatorTarget;
	}
	
	/**
	 * Set currently targeted entity to an arbitrary entity
	 * @param newTarget LivingEntity to target
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target target(LivingEntity newTarget) {
		return new Target(newTarget, holder, activatorTarget, savedTargets);
	}
	
	/**
	 * Set currently targeted entity to a saved entity from this Target's shared save map
	 * @param saveName Save name of saved entity to target
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target targetSaved(String saveName) {
		return new Target(savedTargets.get(saveName), holder, activatorTarget, savedTargets);
	}
	
	/**
	 * Set currently targeted entity to the holder
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target targetHolder() {
		return new Target(holder, holder, activatorTarget, savedTargets);
	}
	
	/**
	 * Set currently targeted entity to the activator
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target targetActivator() {
		return new Target(activatorTarget, holder, activatorTarget, savedTargets);
	}
}
